package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.impl.WalletImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestVersion {

	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		blockchain.start();

		Wallet wallet1 = new WalletImpl(blockchain);
		assertEquals(100, wallet1.getinfo().getVersion());
		assertEquals(100, wallet1.getinfo().getProtocolversion());
	}

}
