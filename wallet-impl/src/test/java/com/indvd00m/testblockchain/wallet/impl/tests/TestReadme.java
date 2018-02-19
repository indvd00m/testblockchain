package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.impl.WalletImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestReadme {

	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		Wallet wallet1 = new WalletImpl(blockchain, new BigDecimal("3"));
		Wallet wallet2 = new WalletImpl(blockchain, new BigDecimal("5.5"));
		blockchain.start();

		waitNextBlock(blockchain);

		assertEquals(0, wallet1.getbalance().compareTo(new BigDecimal("3")));
		assertEquals(0, wallet2.getbalance().compareTo(new BigDecimal("5.5")));

		String address1_1 = wallet1.getnewaddress();
		wallet2.sendtoaddress(address1_1, new BigDecimal("1.005"));
		waitNextBlock(blockchain);

		assertEquals(0, wallet1.getbalance().compareTo(new BigDecimal("4.005")));
		assertEquals(0, wallet2.getbalance().compareTo(new BigDecimal("4.495")));
	}

	protected void waitNextBlock(Blockchain blockchain) throws InterruptedException {
		int confirmations = blockchain.getConfirmations();
		while (confirmations == blockchain.getConfirmations()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

}
