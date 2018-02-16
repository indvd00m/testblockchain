package com.indvd00m.testblockchain.blockchain.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.api.Transaction;
import com.indvd00m.testblockchain.blockchain.api.TransactionException;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestBalance {

	@Test
	public void test01() throws InterruptedException, TransactionException {
		Blockchain blockchain = new BlockchainImpl(100) {

			@Override
			protected void generateBlock() {
				super.generateBlock();
				for (Entry<String, BigDecimal> e : getAddressesBalances().entrySet()) {
					String address = e.getKey();
					BigDecimal balance = e.getValue();
					assertEqualsComparable(balance, getBalance(address));
				}
			}

		};
		blockchain.start();
		assertNull(blockchain.getBalance("illegal address"));

		String address1 = blockchain.generateAddress();
		String address2 = blockchain.generateAddress();
		assertNotNull(address1);
		assertNotNull(address2);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(null, address1, new BigDecimal("2.00"));

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address2));

		{
			blockchain.stop();
			waitBlockchainStop(blockchain);
			BigDecimal balance = blockchain.getBalance(address1);
			Transaction lastTransaction = blockchain.getLastTransaction(address1);
			int conf = blockchain.getConfirmations(lastTransaction.getBlock().getHash());
			assertEqualsComparable(balance, blockchain.getBalance(address1, conf));
			Transaction prevTransaction = blockchain.getLastTransaction(address1, conf + 1);
			int prevConf = blockchain.getConfirmations(prevTransaction.getBlock().getHash());
			assertNotEqualsComparable(balance, blockchain.getBalance(address1, prevConf));
			blockchain.start();
		}

		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(Arrays.asList(address1, address2)));
	}

	@Test(expected = TransactionException.class)
	public void test02() throws InterruptedException, TransactionException {
		Blockchain blockchain = new BlockchainImpl(100) {

			@Override
			protected void generateBlock() {
				super.generateBlock();
				for (Entry<String, BigDecimal> e : getAddressesBalances().entrySet()) {
					String address = e.getKey();
					BigDecimal balance = e.getValue();
					assertEqualsComparable(balance, getBalance(address));
				}
			}

		};
		blockchain.start();
		assertNull(blockchain.getBalance("illegal address"));

		String address1 = blockchain.generateAddress();
		String address2 = blockchain.generateAddress();
		assertNotNull(address1);
		assertNotNull(address2);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(null, address1, new BigDecimal("2.00"));

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
	}

	@Test(expected = TransactionException.class)
	public void test03() throws InterruptedException, TransactionException {
		Blockchain blockchain = new BlockchainImpl(100) {

			@Override
			protected void generateBlock() {
				super.generateBlock();
				for (Entry<String, BigDecimal> e : getAddressesBalances().entrySet()) {
					String address = e.getKey();
					BigDecimal balance = e.getValue();
					assertEqualsComparable(balance, getBalance(address));
				}
			}

		};
		blockchain.start();
		assertNull(blockchain.getBalance("illegal address"));

		String address1 = blockchain.generateAddress();
		String address2 = blockchain.generateAddress();
		assertNotNull(address1);
		assertNotNull(address2);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(null, address1, new BigDecimal("2.00"));

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address2));

		blockchain.addTransactionRequest(address2, "illegal address", new BigDecimal("1.00"));
	}

	protected <T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
		}
	}

	protected <T extends Comparable<T>> void assertNotEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) == 0) {
			throw new AssertionError(String.format("Values should be different. Actual: %s.", t1.toString()));
		}
	}

	protected void waitBlockchainNextBlock(Blockchain blockchain) throws InterruptedException {
		int confirmations = blockchain.getConfirmations();
		while (confirmations == blockchain.getConfirmations()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

	protected void waitBlockchainStop(Blockchain blockchain) throws InterruptedException {
		while (blockchain.isStarted()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}
}
