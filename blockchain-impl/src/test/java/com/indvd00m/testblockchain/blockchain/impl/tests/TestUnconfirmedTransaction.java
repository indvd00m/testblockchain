package com.indvd00m.testblockchain.blockchain.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.api.TransactionException;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestUnconfirmedTransaction {

	@Test
	public void test01() throws InterruptedException, TransactionException {
		Blockchain blockchain = new BlockchainImpl(1000);
		blockchain.start();

		String address1 = blockchain.generateAddress();
		String address2 = blockchain.generateAddress();
		assertNotNull(address1);
		assertNotNull(address2);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.stop();
		waitBlockchainStop(blockchain);
		String txid = blockchain.addTransactionRequest(null, address1, new BigDecimal("2.00"));
		assertNotNull(blockchain.getLastTransaction(address1));
		assertNotNull(blockchain.getTransaction(txid));
		blockchain.start();

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address2));

		blockchain.stop();
		waitBlockchainStop(blockchain);
		txid = blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		assertNotNull(blockchain.getLastTransaction(address2));
		assertNotNull(blockchain.getTransaction(txid));
		blockchain.start();

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getBalance(address2));

		txid = blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		assertNotNull(blockchain.getTransaction(txid));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getBalance(address1));
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getBalance(address2));
	}

	protected <T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
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
