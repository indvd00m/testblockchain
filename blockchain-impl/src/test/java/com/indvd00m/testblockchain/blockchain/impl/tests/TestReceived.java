package com.indvd00m.testblockchain.blockchain.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.api.TransactionException;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestReceived {

	@Test
	public void test() throws InterruptedException, TransactionException {
		Blockchain blockchain = new BlockchainImpl(100);
		blockchain.start();
		assertNull(blockchain.getReceived("illegal address"));

		String address1 = blockchain.generateAddress();
		String address2 = blockchain.generateAddress();
		assertNotNull(address1);
		assertNotNull(address2);
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getReceived(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getReceived(address2));

		blockchain.addTransactionRequest(null, address1, new BigDecimal("2.00"));

		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getReceived(address1));
		assertEqualsComparable(new BigDecimal("0.00"), blockchain.getReceived(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getReceived(address1));
		assertEqualsComparable(new BigDecimal("1.00"), blockchain.getReceived(address2));

		blockchain.addTransactionRequest(address1, address2, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getReceived(address1));
		assertEqualsComparable(new BigDecimal("2.00"), blockchain.getReceived(address2));

		assertEqualsComparable(new BigDecimal("4.00"), blockchain.getReceived(Arrays.asList(address1, address2)));
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
