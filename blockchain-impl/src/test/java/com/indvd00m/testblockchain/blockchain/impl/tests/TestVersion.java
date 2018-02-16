package com.indvd00m.testblockchain.blockchain.impl.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestVersion {

	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		blockchain.start();
		assertEquals(100, blockchain.getVersion());
	}

}
