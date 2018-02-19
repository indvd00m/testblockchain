package com.indvd00m.testblockchain.wallet.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.indvd00m.testblockchain.blockchain.api.Block;
import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.api.Transaction;
import com.indvd00m.testblockchain.blockchain.api.TransactionException;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.api.model.AddressValidation;
import com.indvd00m.testblockchain.wallet.api.model.ReceivedByAccount;
import com.indvd00m.testblockchain.wallet.api.model.ReceivedByAddress;
import com.indvd00m.testblockchain.wallet.api.model.State;
import com.indvd00m.testblockchain.wallet.api.model.transaction.MultiTransaction;
import com.indvd00m.testblockchain.wallet.api.model.transaction.TransactionCategory;
import com.indvd00m.testblockchain.wallet.api.model.transaction.TransactionDetail;
import com.indvd00m.testblockchain.wallet.api.model.transaction.TransactionsSinceBlock;
import com.indvd00m.testblockchain.wallet.api.model.transaction.UnspentTransaction;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class WalletImpl implements Wallet {

	final int protocolVersion = 100;
	final int walletVersion = 104;

	final Blockchain blockchain;
	final int scale;

	// data
	final Map<String, String> accountsByAddresses = new HashMap<>();
	final Map<String, List<String>> addressesByAccounts = new HashMap<>();
	BigDecimal txFee = BigDecimal.ZERO;

	// lock
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public WalletImpl(Blockchain blockchain) {
		this(blockchain, BigDecimal.ZERO);
	}

	public WalletImpl(Blockchain blockchain, BigDecimal startBalance) {
		this.blockchain = blockchain;
		this.scale = blockchain.getScale();

		if (startBalance.compareTo(BigDecimal.ZERO) > 0) {
			w.lock();
			try {
				String address = getnewaddress();
				blockchain.addTransactionRequest(null, address, startBalance);
			} catch (TransactionException e) {
				throw new RuntimeException(e);
			} finally {
				w.unlock();
			}
		}
	}

	@Override
	public String getaccount(String coinaddress) {
		r.lock();
		try {
			return accountsByAddresses.get(coinaddress);
		} finally {
			r.unlock();
		}
	}

	@Override
	public String getaccountaddress(String account) {
		r.lock();
		try {
			List<String> addresses = addressesByAccounts.get(account);
			if (addresses != null) {
				for (String address : addresses) {
					BigDecimal balance = blockchain.getBalance(address);
					if (balance != null && BigDecimal.ZERO.compareTo(balance) == 0) {
						return address;
					}
				}
			}
		} finally {
			r.unlock();
		}

		w.lock();
		try {
			String address = getnewaddress(account);
			return address;
		} finally {
			w.unlock();
		}
	}

	@Override
	public List<String> getaddressesbyaccount(String account) {
		r.lock();
		try {
			List<String> addresses = addressesByAccounts.get(account);
			if (addresses != null) {
				return addresses;
			}
			return new ArrayList<String>();
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getbalance() {
		r.lock();
		try {
			BigDecimal balance = blockchain.getBalance(accountsByAddresses.keySet());
			if (balance != null) {
				return balance;
			}
			return BigDecimal.ZERO;
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getbalance(String account) {
		r.lock();
		try {
			return getbalance(account, 1l);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getbalance(String account, long minconf) {
		r.lock();
		try {
			List<String> addresses = new ArrayList<>();
			if ("*".equals(account)) {
				addresses.addAll(accountsByAddresses.keySet());
			} else {
				addresses.addAll(addressesByAccounts.get(account));
			}
			if (addresses != null) {
				BigDecimal balance = blockchain.getBalance(addresses, (int) minconf);
				if (balance != null) {
					return balance;
				}
			}
			return BigDecimal.ZERO;
		} finally {
			r.unlock();
		}
	}

	@Override
	public com.indvd00m.testblockchain.wallet.api.model.Block getblock(String hash) {
		r.lock();
		try {
			Block block = blockchain.getBlock(hash);
			if (block != null) {
				com.indvd00m.testblockchain.wallet.api.model.Block b = new com.indvd00m.testblockchain.wallet.api.model.Block();
				b.setConfirmations(blockchain.getConfirmations(hash));
				b.setHash(hash);
				Block nextBlock = blockchain.getBlock(block.getIndex() + 1);
				if (nextBlock != null) {
					b.setNextblockhash(nextBlock.getHash());
				}
				Block previousBlock = block.getPreviousBlock();
				if (previousBlock != null) {
					b.setPreviousblockhash(previousBlock.getHash());
				}
				b.setTime(block.getDate().getTime() / 1000l);
				b.setTx(block.getTransactions().stream().map(t -> t.getTxid()).collect(Collectors.toList()));
				b.setVersion(blockchain.getVersion());
				return b;
			}
			return null;
		} finally {
			r.unlock();
		}
	}

	@Override
	public long getblockcount() {
		r.lock();
		try {
			return blockchain.getChain().size();
		} finally {
			r.unlock();
		}
	}

	@Override
	public String getblockhash(long index) {
		r.lock();
		try {
			Block block = blockchain.getBlock((int) index);
			if (block != null) {
				return block.getHash();
			}
			return null;
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getdifficulty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public State getinfo() {
		r.lock();
		try {
			State state = new State();
			state.setBalance(getbalance());
			state.setBlocks((int) getblockcount());
			state.setPaytxfee(txFee);
			state.setProtocolversion(protocolVersion);
			state.setTestnet(true);
			state.setVersion(blockchain.getVersion());
			state.setWalletversion(walletVersion);
			return state;
		} finally {
			r.unlock();
		}
	}

	@Override
	public String getnewaddress() {
		w.lock();
		try {
			return getnewaddress("");
		} finally {
			w.unlock();
		}
	}

	@Override
	public String getnewaddress(String account) {
		w.lock();
		try {
			String address = blockchain.generateAddress();

			accountsByAddresses.put(address, account);
			addressesByAccounts.compute(account, (a, addresses) -> {
				if (addresses == null) {
					addresses = new ArrayList<>();
				}
				addresses.add(address);
				return addresses;
			});

			return address;
		} finally {
			w.unlock();
		}
	}

	@Override
	public BigDecimal getreceivedbyaccount(String account) {
		r.lock();
		try {
			return getreceivedbyaccount(account, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getreceivedbyaccount(String account, long minconf) {
		r.lock();
		try {
			BigDecimal received = BigDecimal.ZERO;
			Collection<String> addresses = null;
			if (account != null) {
				addresses = addressesByAccounts.get(account);
			} else {
				addresses = accountsByAddresses.keySet();
			}
			if (addresses != null) {
				received = blockchain.getReceived(addresses, (int) minconf);
			}
			return received;
		} finally {
			r.unlock();
		}
	}

	@Override
	public BigDecimal getreceivedbyaddress(String coinaddress) {
		return getreceivedbyaddress(coinaddress, 1);
	}

	@Override
	public BigDecimal getreceivedbyaddress(String coinaddress, long minconf) {
		r.lock();
		try {
			BigDecimal received = BigDecimal.ZERO;
			if (accountsByAddresses.containsKey(coinaddress)) {
				received = blockchain.getReceived(coinaddress, (int) minconf);
			}
			return received;
		} finally {
			r.unlock();
		}
	}

	@Override
	public MultiTransaction gettransaction(String txid) {
		r.lock();
		try {
			Transaction transaction = blockchain.getTransaction(txid);
			Block block = transaction.getBlock();
			Collection<String> walletAddresses = accountsByAddresses.keySet();

			MultiTransaction multiTransaction = new MultiTransaction();
			multiTransaction.setAmount(transaction.getAmount());
			if (block != null) {
				multiTransaction.setBlockhash(block.getHash());
				multiTransaction.setBlockindex(block.getIndex());
				multiTransaction.setBlocktime(block.getDate().getTime() / 1000l);
				multiTransaction.setConfirmations(blockchain.getConfirmations(block.getHash()));
			} else {
				multiTransaction.setConfirmations(0);
			}
			multiTransaction.setFee(transaction.getFee());
			multiTransaction.setTime(transaction.getDate().getTime() / 1000l);
			multiTransaction.setTimereceived(transaction.getReceivedDate().getTime() / 1000l);
			multiTransaction.setTxid(transaction.getTxid());

			boolean allFromWallet = walletAddresses.containsAll(transaction.getFromAddresses().keySet());

			multiTransaction.setDetails(transaction.getToAddresses().entrySet().stream().map(e -> {
				String toAddress = e.getKey();
				BigDecimal toAmount = e.getValue();

				TransactionDetail td = new TransactionDetail();

				if (allFromWallet && walletAddresses.contains(toAddress)) {
					td.setAccount(accountsByAddresses.get(toAddress));
					td.setCategory(TransactionCategory.move);
				} else if (allFromWallet) {
					td.setAccount(null);
					td.setCategory(TransactionCategory.send);
				} else if (walletAddresses.contains(toAddress)) {
					td.setAccount(accountsByAddresses.get(toAddress));
					td.setCategory(TransactionCategory.receive);
				}
				td.setAddress(toAddress);
				td.setAmount(toAmount);
				td.setFee(transaction.getFee());

				return td;
			}).collect(Collectors.toList()));

			return multiTransaction;
		} finally {
			r.unlock();
		}
	}

	@Override
	public Map<String, BigDecimal> listaccounts() {
		r.lock();
		try {
			return listaccounts(1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public Map<String, BigDecimal> listaccounts(int minconf) {
		r.lock();
		try {
			Map<String, BigDecimal> map = addressesByAccounts.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> blockchain.getBalance(e.getValue(), minconf)));
			return map;
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAccount> listreceivedbyaccount() {
		r.lock();
		try {
			return listreceivedbyaccount(1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAccount> listreceivedbyaccount(long minconf) {
		r.lock();
		try {
			return listreceivedbyaccount(minconf, false);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAccount> listreceivedbyaccount(long minconf, boolean includeempty) {
		r.lock();
		try {
			List<ReceivedByAccount> list = addressesByAccounts.entrySet().stream().map(e -> {
				ReceivedByAccount r = new ReceivedByAccount();
				r.setAccount(e.getKey());
				r.setAmount(blockchain.getReceived(e.getValue(), (int) minconf));
				// TODO calculate confirmations
				r.setConfirmations(minconf);
				return r;
			}).filter(r -> includeempty || r.getAmount() != null && r.getAmount().compareTo(BigDecimal.ZERO) != 0)
					.collect(Collectors.toList());
			return list;
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAddress> listreceivedbyaddress() {
		r.lock();
		try {
			return listreceivedbyaddress(1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAddress> listreceivedbyaddress(long minconf) {
		r.lock();
		try {
			return listreceivedbyaddress(minconf, false);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<ReceivedByAddress> listreceivedbyaddress(long minconf, boolean includeempty) {
		r.lock();
		try {
			List<Block> chain = blockchain.getChain((int) minconf);
			Collection<String> walletAddresses = accountsByAddresses.keySet();
			List<Transaction> transactions = chain.stream().flatMap(b -> b.getTransactions().stream().filter(t -> t
					.getFromAddresses().keySet().stream().filter(walletAddresses::contains).findFirst().isPresent()
					|| t.getToAddresses().keySet().stream().filter(walletAddresses::contains).findFirst().isPresent()))
					.collect(Collectors.toList());

			List<ReceivedByAddress> list = accountsByAddresses.entrySet().stream().map(e -> {
				String address = e.getKey();
				String account = e.getValue();
				ReceivedByAddress r = new ReceivedByAddress();
				r.setAccount(account);
				r.setAddress(address);
				r.setAmount(blockchain.getReceived(address, (int) minconf));

				r.setTxids(transactions.stream().filter(t -> t.getToAddresses().containsKey(address))
						.map(t -> t.getTxid()).collect(Collectors.toList()));
				Transaction lastTransaction = transactions.stream().filter(t -> t.getToAddresses().containsKey(address))
						.reduce((t1, t2) -> t2).orElse(null);
				if (lastTransaction != null) {
					Block block = lastTransaction.getBlock();
					if (block != null) {
						r.setConfirmations(blockchain.getConfirmations(block.getHash()));
					} else {
						r.setConfirmations(0);
					}
				} else {
					r.setConfirmations(0);
				}

				return r;
			}).filter(r -> includeempty || r.getAmount() != null && r.getAmount().compareTo(BigDecimal.ZERO) != 0)
					.collect(Collectors.toList());
			return list;
		} finally {
			r.unlock();
		}
	}

	@Override
	public TransactionsSinceBlock listsinceblock() {
		r.lock();
		try {
			Block block = blockchain.getBlock(0);
			if (block != null) {
				return listsinceblock(block.getHash());
			} else {
				return new TransactionsSinceBlock();
			}
		} finally {
			r.unlock();
		}
	}

	@Override
	public TransactionsSinceBlock listsinceblock(String blockhash) {
		r.lock();
		try {
			return listsinceblock(blockhash, 1);
		} finally {
			r.unlock();
		}
	}

	@Override
	public TransactionsSinceBlock listsinceblock(String blockhash, long targetConfirmations) {
		r.lock();
		try {
			Block sinceBlock = blockchain.getBlock(blockhash);
			List<Block> chain = blockchain.getChain();
			List<Block> subchain = chain.stream().filter(b -> b.getIndex() >= sinceBlock.getIndex())
					.collect(Collectors.toList());
			Collection<String> walletAddresses = accountsByAddresses.keySet();
			List<Transaction> transactions = subchain.stream().flatMap(b -> b.getTransactions().stream().filter(t -> t
					.getFromAddresses().keySet().stream().filter(walletAddresses::contains).findFirst().isPresent()
					|| t.getToAddresses().keySet().stream().filter(walletAddresses::contains).findFirst().isPresent()))
					.collect(Collectors.toList());

			TransactionsSinceBlock tsb = new TransactionsSinceBlock();
			tsb.setTransactions(transactions.stream().map(t -> {
				Block block = t.getBlock();

				MultiTransaction transaction = new MultiTransaction();

				boolean allFromWallet = walletAddresses.containsAll(t.getFromAddresses().keySet());
				Map<String, BigDecimal> toAddresses = t.getToAddresses();
				if (toAddresses.size() > 1) {
					transaction.setDetails(toAddresses.entrySet().stream().map(e -> {
						String toAddress = e.getKey();
						BigDecimal toAmount = e.getValue();

						TransactionDetail td = new TransactionDetail();

						if (allFromWallet && walletAddresses.contains(toAddress)) {
							td.setAccount(accountsByAddresses.get(toAddress));
							td.setCategory(TransactionCategory.move);
						} else if (allFromWallet) {
							td.setAccount(null);
							td.setCategory(TransactionCategory.send);
						} else if (walletAddresses.contains(toAddress)) {
							td.setAccount(accountsByAddresses.get(toAddress));
							td.setCategory(TransactionCategory.receive);
						}
						td.setAddress(toAddress);
						td.setAmount(toAmount);
						td.setFee(transaction.getFee());

						return td;
					}).collect(Collectors.toList()));
				} else {
					toAddresses.entrySet().stream().findFirst().ifPresent(e -> {
						String toAddress = e.getKey();

						if (allFromWallet && walletAddresses.contains(toAddress)) {
							transaction.setAccount(accountsByAddresses.get(toAddress));
							transaction.setCategory(TransactionCategory.move);
						} else if (allFromWallet) {
							transaction.setAccount(null);
							transaction.setCategory(TransactionCategory.send);
						} else if (walletAddresses.contains(toAddress)) {
							transaction.setAccount(accountsByAddresses.get(toAddress));
							transaction.setCategory(TransactionCategory.receive);
						}
						transaction.setAddress(toAddress);
					});
				}

				transaction.setAmount(t.getAmount());
				if (block != null) {
					transaction.setBlockhash(block.getHash());
					transaction.setBlockindex(block.getIndex());
					transaction.setBlocktime(block.getDate().getTime() / 1000l);
					transaction.setConfirmations(blockchain.getConfirmations(block.getHash()));
				} else {
					transaction.setConfirmations(0);
				}
				transaction.setFee(t.getFee());
				transaction.setTime(t.getDate().getTime() / 1000l);
				transaction.setTimereceived(t.getReceivedDate().getTime() / 1000l);
				transaction.setTxid(t.getTxid());

				return transaction;
			}).collect(Collectors.toList()));

			tsb.setLastblock(IntStream.range(0, subchain.size()).mapToObj(i -> subchain.get(subchain.size() - i - 1))
					.filter(b -> blockchain.getConfirmations(b.getHash()) >= targetConfirmations).findFirst()
					.map(b -> b.getHash()).orElse(null));
			return tsb;
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction> listtransactions() {
		r.lock();
		try {
			return listtransactions("*");
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction> listtransactions(String account) {
		r.lock();
		try {
			return listtransactions(account, 10);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction> listtransactions(String account,
			long count) {
		r.lock();
		try {
			return listtransactions(account, count, 0);
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction> listtransactions(String account,
			long count, long from) {
		r.lock();
		try {
			final Set<String> addresses = new HashSet<>();
			if ("*".equals(account)) {
				addresses.addAll(accountsByAddresses.keySet());
			} else {
				addresses.addAll(addressesByAccounts.get(account));
			}
			List<Block> chain = blockchain.getChain();
			List<Transaction> transactions = IntStream.range(0, chain.size())
					.mapToObj(i -> chain.get(chain.size() - i - 1))
					.flatMap(b -> IntStream.range(0, b.getTransactions().size())
							.mapToObj(i -> b.getTransactions().get(b.getTransactions().size() - i - 1)))
					.filter(t -> t.getToAddresses().entrySet().stream().filter(e -> addresses.contains(e.getKey()))
							.findFirst().isPresent()
							|| t.getFromAddresses().entrySet().stream().filter(e -> addresses.contains(e.getKey()))
									.findFirst().isPresent())
					.collect(Collectors.toList());
			if (from < transactions.size()) {
				if (from + count <= transactions.size()) {
					transactions = transactions.subList((int) from, (int) (from + count));
				} else {
					transactions = transactions.subList((int) from, transactions.size());
				}
			} else {
				transactions = new ArrayList<>();
			}
			Collection<String> walletAddresses = accountsByAddresses.keySet();
			List<com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction> list = transactions.stream()
					.map(t -> {
						Block block = t.getBlock();

						MultiTransaction transaction = new MultiTransaction();

						boolean allFromWallet = walletAddresses.containsAll(t.getFromAddresses().keySet());
						Map<String, BigDecimal> toAddresses = t.getToAddresses();
						if (toAddresses.size() > 1) {
							transaction.setDetails(toAddresses.entrySet().stream().map(e -> {
								String toAddress = e.getKey();
								BigDecimal toAmount = e.getValue();

								TransactionDetail td = new TransactionDetail();

								if (allFromWallet && walletAddresses.contains(toAddress)) {
									td.setAccount(accountsByAddresses.get(toAddress));
									td.setCategory(TransactionCategory.move);
								} else if (allFromWallet) {
									td.setAccount(null);
									td.setCategory(TransactionCategory.send);
								} else if (walletAddresses.contains(toAddress)) {
									td.setAccount(accountsByAddresses.get(toAddress));
									td.setCategory(TransactionCategory.receive);
								}
								td.setAddress(toAddress);
								td.setAmount(toAmount);
								td.setFee(transaction.getFee());

								return td;
							}).collect(Collectors.toList()));
						} else {
							toAddresses.entrySet().stream().findFirst().ifPresent(e -> {
								String toAddress = e.getKey();

								if (allFromWallet && walletAddresses.contains(toAddress)) {
									transaction.setAccount(accountsByAddresses.get(toAddress));
									transaction.setCategory(TransactionCategory.move);
								} else if (allFromWallet) {
									transaction.setAccount(null);
									transaction.setCategory(TransactionCategory.send);
								} else if (walletAddresses.contains(toAddress)) {
									transaction.setAccount(accountsByAddresses.get(toAddress));
									transaction.setCategory(TransactionCategory.receive);
								}
								transaction.setAddress(toAddress);
							});
						}

						transaction.setAmount(t.getAmount());
						if (block != null) {
							transaction.setBlockhash(block.getHash());
							transaction.setBlockindex(block.getIndex());
							transaction.setBlocktime(block.getDate().getTime() / 1000l);
							transaction.setConfirmations(blockchain.getConfirmations(block.getHash()));
						} else {
							transaction.setConfirmations(0);
						}
						transaction.setFee(t.getFee());
						transaction.setTime(t.getDate().getTime() / 1000l);
						transaction.setTimereceived(t.getReceivedDate().getTime() / 1000l);
						transaction.setTxid(t.getTxid());

						return transaction;
					}).collect(Collectors.toList());
			return list;
		} finally {
			r.unlock();
		}
	}

	@Override
	public List<UnspentTransaction> listunspent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<UnspentTransaction> listunspent(long minconf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<UnspentTransaction> listunspent(long minconf, long maxconf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<UnspentTransaction> listunspent(long minconf, long maxconf, String... addresses) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean move(String fromaccount, String toaccount, BigDecimal amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean move(String fromaccount, String toaccount, BigDecimal amount, long minconf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean move(String fromaccount, String toaccount, BigDecimal amount, long minconf, String comment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount, long minconf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount, long minconf, String comment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount, long minconf, String comment,
			String commentto) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts, long minconf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts, long minconf, String comment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String sendtoaddress(String coinaddress, BigDecimal amount) {
		w.lock();
		try {
			return sendtoaddress(coinaddress, amount, null);
		} finally {
			w.unlock();
		}
	}

	@Override
	public String sendtoaddress(String coinaddress, BigDecimal amount, String comment) {
		w.lock();
		try {
			return sendtoaddress(coinaddress, amount, comment, null);
		} finally {
			w.unlock();
		}
	}

	@Override
	public String sendtoaddress(String coinaddress, BigDecimal amount, String comment, String commentTo) {
		w.lock();
		try {
			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new RuntimeException(String.format("Amount must be positive: %s", amount.toPlainString()));
			}
			if (amount.scale() > scale) {
				BigDecimal fixedAmount = amount.setScale(scale, RoundingMode.DOWN);
				if (amount.compareTo(fixedAmount) == 0) {
					amount = fixedAmount;
				} else {
					// ok, pass illegal value to blockchain
				}
			}
			Map<String, BigDecimal> balances = accountsByAddresses.keySet().stream()
					.collect(Collectors.toMap(Function.identity(), a -> blockchain.getBalance(a)));
			Map<String, BigDecimal> positiveBalances = balances.entrySet().stream()
					.filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			// from addresses
			Map<String, BigDecimal> fromAddresses = new HashMap<>();
			BigDecimal residue = amount.add(txFee);
			for (Entry<String, BigDecimal> e : positiveBalances.entrySet()) {
				String fromAddress = e.getKey();
				BigDecimal balance = e.getValue();
				BigDecimal fromAmount;
				if (residue.compareTo(balance) >= 0) {
					fromAmount = balance;
					residue = residue.subtract(balance);
				} else {
					fromAmount = residue;
					residue = BigDecimal.ZERO;
				}
				fromAddresses.put(fromAddress, fromAmount);
				if (residue.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// to addresses
			Map<String, BigDecimal> toAddresses = new HashMap<>();
			toAddresses.put(coinaddress, amount);

			String txid;
			try {
				txid = blockchain.addTransactionRequest(fromAddresses, toAddresses, amount, txFee);
			} catch (TransactionException e) {
				throw new RuntimeException(e);
			}
			return txid;
		} finally {
			w.unlock();
		}
	}

	@Override
	public void setaccount(String coinaddress, String account) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean settxfee(BigDecimal amount) {
		w.lock();
		try {
			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				return false;
			}
			txFee = amount;
			return true;
		} finally {
			w.unlock();
		}
	}

	@Override
	public String signmessage(String coinaddress, String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AddressValidation validateaddress(String coinaddress) {
		r.lock();
		try {
			AddressValidation v = new AddressValidation();
			v.setAccount(accountsByAddresses.get(coinaddress));
			v.setAddress(coinaddress);
			v.setIsvalid(blockchain.isValid(coinaddress));
			return v;
		} finally {
			r.unlock();
		}
	}

	@Override
	public boolean verifymessage(String coinaddress, String signature, String message) {
		throw new UnsupportedOperationException();
	}

}
