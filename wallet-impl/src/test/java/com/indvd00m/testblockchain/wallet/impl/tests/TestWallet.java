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
public class TestWallet {

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
		wallet2.sendtoaddress(address1_1, new BigDecimal("1.05"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.05"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("8.95"), wallet2.getbalance());

		String address1_2 = wallet1.getnewaddress();
		wallet2.sendtoaddress(address1_2, new BigDecimal("0.00000001"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("1.05000001"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("8.94999999"), wallet2.getbalance());

		String address2_1 = wallet2.getnewaddress();
		wallet1.sendtoaddress(address2_1, new BigDecimal("1"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.05000001"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("9.94999999"), wallet2.getbalance());

		String address2_2 = wallet2.getnewaddress();
		wallet1.sendtoaddress(address2_2, new BigDecimal("0.00000001"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("0.05"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("9.95"), wallet2.getbalance());

		wallet2.settxfee(new BigDecimal("1.00"));
		wallet2.sendtoaddress(address1_1, new BigDecimal("3.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("3.05"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("5.95"), wallet2.getbalance());

		wallet2.sendtoaddress(address1_1, new BigDecimal("3.00"));
		waitBlockchainNextBlock(blockchain);
		assertEqualsComparable(new BigDecimal("6.05"), wallet1.getbalance());
		assertEqualsComparable(new BigDecimal("1.95"), wallet2.getbalance());

		assertEqualsComparable(new BigDecimal("7.05"), wallet1.getreceivedbyaddress(address1_1));
		assertEqualsComparable(new BigDecimal("0.00000001"), wallet1.getreceivedbyaddress(address1_2));
		assertEqualsComparable(new BigDecimal("1.00"), wallet2.getreceivedbyaddress(address2_1));
		assertEqualsComparable(new BigDecimal("0.00000001"), wallet2.getreceivedbyaddress(address2_2));

		assertEqualsComparable(BigDecimal.ZERO, wallet2.getreceivedbyaddress(address1_1));
		assertEqualsComparable(BigDecimal.ZERO, wallet2.getreceivedbyaddress(address1_2));
		assertEqualsComparable(BigDecimal.ZERO, wallet1.getreceivedbyaddress(address2_1));
		assertEqualsComparable(BigDecimal.ZERO, wallet1.getreceivedbyaddress(address2_2));
	}

	protected void waitBlockchainNextBlock(Blockchain blockchain) throws InterruptedException {
		int confirmations = blockchain.getConfirmations();
		while (confirmations == blockchain.getConfirmations()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

	protected <T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
		}
	}

}
