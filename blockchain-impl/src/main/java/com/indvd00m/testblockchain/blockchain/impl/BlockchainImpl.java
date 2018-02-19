package com.indvd00m.testblockchain.blockchain.impl;

import static com.indvd00m.testblockchain.commons.lambda.LambdaExceptionUtil.rethrowConsumer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indvd00m.testblockchain.blockchain.api.Block;
import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.api.Transaction;
import com.indvd00m.testblockchain.blockchain.api.TransactionException;
import com.indvd00m.testblockchain.blockchain.api.TransactionRequest;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class BlockchainImpl implements Blockchain {

	protected static final Logger log = LoggerFactory.getLogger(BlockchainImpl.class);

	final int version = 100;

	// thread
	Thread thread = null;

	// data
	private final List<Block> chain = new ArrayList<>();
	protected final long blockGenerationPeriodMillis;
	protected final int scale;
	private final List<TransactionRequest> pendingTransactions = new ArrayList<>();

	// cache
	private final Map<String, BigDecimal> addressesBalances = new HashMap<>();
	private final Map<String, Block> blocksByHash = new HashMap<>();
	private final Map<String, Transaction> transactionsByTxid = new HashMap<>();

	// lock
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public BlockchainImpl(long blockGenerationPeriodMillis) {
		this(blockGenerationPeriodMillis, DEFAULT_SCALE);
	}

	public BlockchainImpl(long blockGenerationPeriodMillis, int scale) {
		if (blockGenerationPeriodMillis <= 0) {
			throw new IllegalArgumentException();
		}
		this.blockGenerationPeriodMillis = blockGenerationPeriodMillis;
		this.scale = scale;
	}

	@Override
	public boolean isStarted() {
		r.lock();
		try {
			return thread != null && !thread.isInterrupted();
		} finally {
			r.unlock();
		}
	}

	@Override
	public void start() {
		if (thread != null) {
			return;
		}
		w.lock();
		try {
			if (thread != null) {
				return;
			}
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (!Thread.currentThread().isInterrupted()) {
							generateBlock();
							TimeUnit.MILLISECONDS.sleep(blockGenerationPeriodMillis);
						}
					} catch (InterruptedException e) {

					} finally {
						log.debug("Block generation stopped");
					}
				}

			});
			log.debug("Starting block generation");
			thread.start();
		} finally {
			w.unlock();
		}
	}

	@Override
	public void stop() {
		w.lock();
		try {
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
		} finally {
			w.unlock();
		}
	}

	protected void generateBlock() {
		w.lock();
		try {
			List<TransactionRequest> transactionsRequests = new ArrayList<>(pendingTransactions);
			Map<String, BigDecimal> localBalances = new HashMap<>();
			List<Transaction> transactions = new ArrayList<>();
			Date currentDate = new Date();

			// block's fee will be sent to nobody's address
			String feeAddress = generateAddress();
			localBalances.put(feeAddress, BigDecimal.ZERO);

			// transactions
			for (TransactionRequest transactionRequest : transactionsRequests) {
				try {
					Transaction transaction = processTransactionRequest(transactionRequest, localBalances, currentDate);
					BigDecimal fee = transaction.getFee();
					if (fee.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal feeBalance = localBalances.get(feeAddress);
						feeBalance = feeBalance.add(fee);
						localBalances.put(feeAddress, feeBalance);
					}
					transactions.add(transaction);
				} catch (TransactionException e) {
					log.error(String.format("Transaction error: %s", e.getMessage()));
				} catch (Exception e) {
					log.error("ERROR", e);
				}
			}

			// block awards
			BigDecimal feeBalance = localBalances.get(feeAddress);
			if (!transactions.isEmpty() && feeBalance.compareTo(BigDecimal.ZERO) > 0) {
				String txid = generateTxid();
				Map<String, BigDecimal> toAddresses = new HashMap<>();
				toAddresses.put(feeAddress, feeBalance);
				Transaction transaction = new TransactionImpl(txid, new HashMap<>(), toAddresses, feeBalance,
						BigDecimal.ZERO, currentDate, currentDate);
				transactions.add(0, transaction);
			}

			// block
			Block previousBlock = null;
			if (!chain.isEmpty()) {
				previousBlock = chain.get(chain.size() - 1);
			}

			StringBuilder source = new StringBuilder();
			source.append(new RandomStringGenerator.Builder().build().generate(32));
			if (previousBlock != null) {
				source.append(";").append(previousBlock.getHash());
			}
			source.append(";").append(currentDate.getTime());
			for (Transaction transaction : transactions) {
				source.append(";").append(transaction.getTxid());
			}
			String hash = DigestUtils.md5Hex(source.toString());

			Block block = new BlockImpl(chain.size(), hash, currentDate, previousBlock, transactions);

			// commit result
			chain.add(block);
			blocksByHash.put(block.getHash(), block);
			transactions.stream().forEachOrdered(transaction -> {
				((TransactionImpl) transaction).block = block;
				transactionsByTxid.put(transaction.getTxid(), transaction);
				long fromAddressesCount = transaction.getFromAddresses().keySet().stream().filter(a -> a != null)
						.count();
				log.debug(String.format("New transaction %s: %s from %d addresses to %d addresses",
						transaction.getTxid(), transaction.getAmount().toPlainString(), fromAddressesCount,
						transaction.getToAddresses().size()));
			});
			localBalances.entrySet().stream().forEach(e -> {
				String address = e.getKey();
				BigDecimal balance = e.getValue();
				addressesBalances.put(address, balance);
			});
			pendingTransactions.removeAll(transactionsRequests);
		} finally {
			w.unlock();
		}
	}

	private Transaction processTransactionRequest(TransactionRequest transactionRequest,
			Map<String, BigDecimal> localBalances, Date currentDate) throws TransactionException {

		r.lock();
		try {

			Map<String, BigDecimal> fromAddresses = transactionRequest.getFromAddresses();
			Map<String, BigDecimal> toAddresses = transactionRequest.getToAddresses();
			BigDecimal amount = transactionRequest.getAmount();
			BigDecimal fee = transactionRequest.getFee();

			// check conditions
			checkTransaction(fromAddresses, toAddresses, amount, fee);

			// transfer coins
			fromAddresses.entrySet().stream().forEach(e -> {
				String fromAddress = e.getKey();
				if (fromAddress != null) {
					BigDecimal fromAmount = e.getValue();
					BigDecimal fromBalance = addressesBalances.get(fromAddress);
					localBalances.put(fromAddress, fromBalance.subtract(fromAmount));
				}
			});
			toAddresses.entrySet().stream().forEach(e -> {
				String toAddress = e.getKey();
				BigDecimal toAmount = e.getValue();
				BigDecimal toBalance = addressesBalances.get(toAddress);
				localBalances.put(toAddress, toBalance.add(toAmount));
			});

			// transaction
			Transaction transaction = new TransactionImpl(transactionRequest.getTxid(), fromAddresses, toAddresses,
					amount, fee, transactionRequest.getDate(), currentDate);
			return transaction;

		} finally {
			r.unlock();
		}
	}

	private boolean checkTransaction(Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount, BigDecimal fee) throws TransactionException {
		r.lock();
		try {
			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new TransactionException(String.format("Amount must be positive: %s", amount.toPlainString()));
			}
			if (fee.compareTo(BigDecimal.ZERO) < 0) {
				throw new TransactionException(String.format("Fee can't be negative: %s", fee.toPlainString()));
			}
			if (amount.scale() > scale) {
				throw new TransactionException(String.format("Expected amount scale: %d, but actual: %d (%s)", scale,
						amount.scale(), amount.toPlainString()));
			}
			if (fee.scale() > scale) {
				throw new TransactionException(String.format("Expected fee scale: %d, but actual: %d (%s)", scale,
						fee.scale(), fee.toPlainString()));
			}
			fromAddresses.keySet().stream().filter(a -> a != null && !isValid(a)).findFirst()
					.ifPresent(rethrowConsumer(a -> {
						throw new TransactionException(String.format("From address incorrect: %s", a));
					}));
			toAddresses.keySet().stream().filter(a -> !isValid(a)).findFirst().ifPresent(rethrowConsumer(a -> {
				throw new TransactionException(String.format("To address incorrect: %s", a));
			}));
			fromAddresses.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) <= 0).findFirst()
					.ifPresent(rethrowConsumer(e -> {
						throw new TransactionException(
								String.format("Sending of negative or zero amount is deprecated: %s from %s",
										e.getValue().toPlainString(), e.getKey()));
					}));
			toAddresses.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) <= 0).findFirst()
					.ifPresent(rethrowConsumer(e -> {
						throw new TransactionException(
								String.format("Receiving of negative or zero amount is deprecated: %s to %s",
										e.getValue().toPlainString(), e.getKey()));
					}));
			BigDecimal fromSum = fromAddresses.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
			if (fromSum.compareTo(amount.add(fee)) != 0) {
				throw new TransactionException(String.format("Expected from sum (amount + fee): %s, but actual: %s",
						amount.add(fee).toPlainString(), fromSum.toPlainString()));
			}
			BigDecimal toSum = toAddresses.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
			if (toSum.compareTo(amount) != 0) {
				throw new TransactionException(String.format("Expected to sum (amount): %s, but actual: %s",
						amount.toPlainString(), toSum.toPlainString()));
			}
			fromAddresses.entrySet().stream().filter(e -> {
				String fromAddress = e.getKey();
				return fromAddress != null && addressesBalances.get(fromAddress).compareTo(e.getValue()) < 0;
			}).findFirst().ifPresent(rethrowConsumer(e -> {
				throw new TransactionException(String.format("Address %s has insufficient funds: %s (%s expected)",
						e.getKey(), addressesBalances.get(e.getKey()).toPlainString(), e.getValue().toPlainString()));
			}));

			return true;
		} finally {
			r.unlock();
		}
	}

	protected Map<String, BigDecimal> getAddressesBalances() {
		r.lock();
		try {
			return Collections.unmodifiableMap(addressesBalances);
		} finally {
			r.unlock();
		}
	}

	@Override
	public String addTransactionRequest(String fromAddress, String toAddress, BigDecimal amount)
			throws TransactionException {
		w.lock();
		try {
			return addTransactionRequest(fromAddress, toAddress, amount, BigDecimal.ZERO);
		} finally {
			w.unlock();
		}
	}

	@Override
	public String addTransactionRequest(String fromAddress, String toAddress, BigDecimal amount, BigDecimal fee)
			throws TransactionException {
		w.lock();
		try {
			Map<String, BigDecimal> fromAddresses = new HashMap<>();
			Map<String, BigDecimal> toAddresses = new HashMap<>();
			fromAddresses.put(fromAddress, amount.add(fee));
			toAddresses.put(toAddress, amount);

			checkTransaction(fromAddresses, toAddresses, amount, fee);

			String txid = generateTxid();
			TransactionRequest transaction = new TransactionRequestImpl(txid, fromAddresses, toAddresses, amount, fee,
					new Date());
			pendingTransactions.add(transaction);

			return txid;
		} finally {
			w.unlock();
		}
	}

	@Override
	public String addTransactionRequest(Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount) throws TransactionException {
		w.lock();
		try {
			return addTransactionRequest(fromAddresses, toAddresses, amount, BigDecimal.ZERO);
		} finally {
			w.unlock();
		}
	}

	@Override
	public String addTransactionRequest(Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount, BigDecimal fee) throws TransactionException {
		w.lock();
		try {
			checkTransaction(fromAddresses, toAddresses, amount, fee);

			String txid = generateTxid();
			TransactionRequest transaction = new TransactionRequestImpl(txid, fromAddresses, toAddresses, amount, fee,
					new Date());
			pendingTransactions.add(transaction);

			return txid;
		} finally {
			w.unlock();
		}
	}

	protected String generateTxid() {
		RandomStringGenerator generator = new RandomStringGenerator.Builder().build();
		String source1 = generator.generate(64);
		String source2 = generator.generate(64);
		String part1 = DigestUtils.md5Hex(source1);
		String part2 = DigestUtils.md5Hex(source2);
		String txid = part1 + part2;
		return txid;
	}

	@Override
	public String generateAddress() {
		w.lock();
		try {
			String source = new RandomStringGenerator.Builder().build().generate(32);
			String address = DigestUtils.md5Hex(source);

			addressesBalances.put(address, BigDecimal.ZERO);

			return address;
		} finally {
			w.unlock();
		}
	}

	@Override
	public int getConfirmations() {
		r.lock();
		try {
			return chain.size();
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<Block> getChain() {
		r.lock();
		try {
			ArrayList<Block> chainCopy = new ArrayList<>(chain);
			return Collections.unmodifiableList(chainCopy);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<Block> getChain(int confirmations) {
		r.lock();
		try {
			List<Block> list = new ArrayList<>();
			if (confirmations <= chain.size()) {
				list = new ArrayList<>(chain.subList(0, chain.size() - confirmations + 1));
			}
			return Collections.unmodifiableList(list);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getBalance(String address) {
		r.lock();
		try {
			return getBalance(address, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getBalance(Collection<String> addresses) {
		r.lock();
		try {
			return getBalance(addresses, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getBalance(String address, int confirmations) {
		r.lock();
		try {
			return getBalance(Arrays.asList(address), confirmations);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getBalance(Collection<String> addresses, int confirmations) {
		r.lock();
		try {
			if (confirmations == 0) {
				return null;
			}
			Set<String> addressesSet = new HashSet<>(addresses);
			List<Block> confirmedChain = getChain(confirmations);
			BigDecimal balance = BigDecimal.ZERO;
			int operationsCount = 0;
			for (Block block : confirmedChain) {
				for (Transaction transaction : block.getTransactions()) {
					BigDecimal transactionSend = transaction.getFromAddresses().entrySet().stream()
							.filter(e -> e.getKey() != null && addressesSet.contains(e.getKey())).map(e -> e.getValue())
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					if (transactionSend.compareTo(BigDecimal.ZERO) > 0) {
						operationsCount++;
						balance = balance.subtract(transactionSend);
					}
					BigDecimal transactionReceived = transaction.getToAddresses().entrySet().stream()
							.filter(e -> addressesSet.contains(e.getKey())).map(e -> e.getValue())
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					if (transactionReceived.compareTo(BigDecimal.ZERO) > 0) {
						operationsCount++;
						balance = balance.add(transactionReceived);
					}
				}
			}
			if (operationsCount == 0) {
				if (!addressesBalances.keySet().containsAll(addressesSet)) {
					return null;
				}
			}
			return balance;
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getReceived(String address) {
		r.lock();
		try {
			return getReceived(address, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getReceived(Collection<String> addresses) {
		r.lock();
		try {
			return getReceived(addresses, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getReceived(String address, int confirmations) {
		r.lock();
		try {
			return getReceived(Arrays.asList(address), confirmations);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getReceived(Collection<String> addresses, int confirmations) {
		r.lock();
		try {
			if (confirmations == 0) {
				return null;
			}
			Set<String> addressesSet = new HashSet<>(addresses);
			List<Block> confirmedChain = getChain(confirmations);
			BigDecimal received = BigDecimal.ZERO;
			int operationsCount = 0;
			for (Block block : confirmedChain) {
				for (Transaction transaction : block.getTransactions()) {
					BigDecimal transactionReceived = transaction.getToAddresses().entrySet().stream()
							.filter(e -> addressesSet.contains(e.getKey())).map(e -> e.getValue())
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					if (transactionReceived.compareTo(BigDecimal.ZERO) > 0) {
						operationsCount++;
						received = received.add(transactionReceived);
					}
				}
			}
			if (operationsCount == 0) {
				if (!addressesBalances.keySet().containsAll(addressesSet)) {
					return null;
				}
			}
			return received;
		} finally {
			r.unlock();
		}
	}

	@Override
	public Transaction getLastTransaction(String address) {
		r.lock();
		try {
			Transaction transaction = getLastTransaction(address, 0);
			if (transaction == null) {
				transaction = getLastTransaction(address, 1);
			}
			return transaction;
		} finally {
			r.unlock();
		}
	}

	@Override
	public Transaction getLastTransaction(String address, int confirmations) {
		r.lock();
		try {
			if (confirmations == 0) {
				TransactionRequest tr = pendingTransactions.stream().filter(
						t -> t.getFromAddresses().containsKey(address) || t.getToAddresses().containsKey(address))
						.findFirst().orElse(null);
				if (tr != null) {
					Map<String, BigDecimal> fromAddresses = tr.getFromAddresses();
					Map<String, BigDecimal> toAddresses = tr.getToAddresses();
					BigDecimal amount = tr.getAmount();
					BigDecimal fee = tr.getFee();
					String txid = tr.getTxid();
					Date date = tr.getDate();

					Transaction t = new TransactionImpl(txid, fromAddresses, toAddresses, amount, fee, date, date);
					return t;
				}
			} else {
				for (int i = chain.size() - confirmations; i >= 0 && i < chain.size(); i--) {
					Block block = chain.get(i);
					List<Transaction> transactions = block.getTransactions();
					for (int j = transactions.size() - 1; j >= 0; j--) {
						Transaction transaction = transactions.get(j);
						if (transaction.getFromAddresses().containsKey(address)) {
							return transaction;
						}
						if (transaction.getToAddresses().containsKey(address)) {
							return transaction;
						}
					}
				}
			}
			return null;
		} finally {
			r.unlock();
		}
	}

	@Override
	public int getConfirmations(String blockHash) {
		r.lock();
		try {
			Block block = blocksByHash.get(blockHash);
			int confirmations = chain.size() - block.getIndex();
			return confirmations;
		} finally {
			r.unlock();
		}
	}

	@Override
	public Block getBlock(String blockHash) {
		r.lock();
		try {
			return blocksByHash.get(blockHash);
		} finally {
			r.unlock();
		}
	}

	@Override
	public Block getBlock(int index) {
		r.lock();
		try {
			if (index < chain.size()) {
				return chain.get(index);
			}
			return null;
		} finally {
			r.unlock();
		}
	}

	@Override
	public Transaction getTransaction(String txid) {
		r.lock();
		try {
			Transaction transaction = transactionsByTxid.get(txid);
			if (transaction == null) {
				TransactionRequest tr = pendingTransactions.stream().filter(t -> txid.equals(t.getTxid())).findFirst()
						.orElse(null);
				if (tr != null) {
					Map<String, BigDecimal> fromAddresses = tr.getFromAddresses();
					Map<String, BigDecimal> toAddresses = tr.getToAddresses();
					BigDecimal amount = tr.getAmount();
					BigDecimal fee = tr.getFee();
					Date date = tr.getDate();

					transaction = new TransactionImpl(txid, fromAddresses, toAddresses, amount, fee, date, date);
				}
			}
			return transaction;
		} finally {
			r.unlock();
		}
	}

	@Override
	public boolean isValid(String address) {
		r.lock();
		try {
			return addressesBalances.containsKey(address);
		} finally {
			r.unlock();
		}
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public int getScale() {
		return scale;
	}
}
