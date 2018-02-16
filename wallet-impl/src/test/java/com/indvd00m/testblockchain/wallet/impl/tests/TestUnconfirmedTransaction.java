package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
public class TestUnconfirmedTransaction {

	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		blockchain.start();

		Wallet wallet1 = new WalletImpl(blockchain);
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(BigDecimal.ZERO, wallet1.getbalance());

		BigDecimal startBalance2 = new BigDecimal("10");
		Wallet wallet2 = new WalletImpl(blockchain, startBalance2);
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(startBalance2, wallet2.getbalance());

		assertEqualsComparable(BigDecimal.ZERO, wallet1.getbalance());

		String address1_1 = wallet1.getnewaddress();
		blockchain.stop();
		waitBlockchainStop(blockchain);
		String txid = wallet2.sendtoaddress(address1_1, new BigDecimal("1.05"));
		assertNotNull(wallet2.gettransaction(txid));
		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.05"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("8.95"), wallet2.getbalance());

		blockchain.stop();
		waitBlockchainStop(blockchain);
		txid = wallet2.sendtoaddress(address1_1, new BigDecimal("0.00000001"));
		assertNotNull(wallet2.gettransaction(txid));
		blockchain.start();
		waitBlockchainNextBlock(blockchain);

		blockchain.stop();
		waitBlockchainStop(blockchain);
		txid = wallet2.sendtoaddress(address1_1, new BigDecimal("0.00000001"));
		assertNotNull(wallet2.gettransaction(txid));
		blockchain.start();
		waitBlockchainNextBlock(blockchain);
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

	protected <T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
		}
	}

}
