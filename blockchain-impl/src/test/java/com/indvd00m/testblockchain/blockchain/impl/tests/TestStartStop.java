package com.indvd00m.testblockchain.blockchain.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestStartStop {

	@Test
	public void test() throws InterruptedException {
		long period = 1000;
		Blockchain blockchain = new BlockchainImpl(period);
		assertFalse(blockchain.isStarted());
		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		assertTrue(blockchain.isStarted());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		assertTrue(blockchain.isStarted());

		blockchain.stop();
		waitBlockchainStop(blockchain);
		assertFalse(blockchain.isStarted());
		int confirmations = blockchain.getConfirmations();
		TimeUnit.MILLISECONDS.sleep(period * 2);
		assertEquals(confirmations, blockchain.getConfirmations());
		assertFalse(blockchain.isStarted());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		assertTrue(blockchain.isStarted());

		blockchain.stop();
		waitBlockchainStop(blockchain);
		assertFalse(blockchain.isStarted());
		confirmations = blockchain.getConfirmations();
		TimeUnit.MILLISECONDS.sleep(period * 2);
		assertEquals(confirmations, blockchain.getConfirmations());
		assertFalse(blockchain.isStarted());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		assertTrue(blockchain.isStarted());
		waitBlockchainNextBlock(blockchain);
		waitBlockchainNextBlock(blockchain);
		waitBlockchainNextBlock(blockchain);
		assertTrue(blockchain.isStarted());

		blockchain.stop();
		waitBlockchainStop(blockchain);
		assertFalse(blockchain.isStarted());
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
