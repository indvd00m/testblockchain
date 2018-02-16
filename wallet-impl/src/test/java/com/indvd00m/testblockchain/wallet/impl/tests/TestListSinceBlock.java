package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.api.model.Block;
import com.indvd00m.testblockchain.wallet.api.model.transaction.MultiTransaction;
import com.indvd00m.testblockchain.wallet.api.model.transaction.TransactionsSinceBlock;
import com.indvd00m.testblockchain.wallet.impl.WalletImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestListSinceBlock {

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

		TransactionsSinceBlock lsb1_1 = wallet1.listsinceblock();
		assertNotNull(lsb1_1);
		assertNotNull(lsb1_1.getLastblock());

		TransactionsSinceBlock lsb2_1 = wallet2.listsinceblock();
		assertNotNull(lsb2_1);
		assertNotNull(lsb2_1.getLastblock());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();

		TransactionsSinceBlock lsb1_2 = wallet1.listsinceblock();
		assertNotNull(lsb1_2);
		assertNotNull(lsb1_2.getLastblock());
		assertNotEquals(lsb1_1.getLastblock(), lsb1_2.getLastblock());

		TransactionsSinceBlock lsb2_2 = wallet2.listsinceblock();
		assertNotNull(lsb2_2);
		assertNotNull(lsb2_2.getLastblock());
		assertNotEquals(lsb2_1.getLastblock(), lsb2_2.getLastblock());

		String txid = wallet2.sendtoaddress(address1_1, new BigDecimal("1.00"));

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();

		TransactionsSinceBlock lsb1_3 = wallet1.listsinceblock();
		assertNotNull(lsb1_3);
		assertNotNull(lsb1_3.getLastblock());
		assertEquals(1, lsb1_3.getTransactions().size());
		assertNotEquals(lsb1_2.getLastblock(), lsb1_3.getLastblock());

		TransactionsSinceBlock lsb2_3 = wallet2.listsinceblock();
		assertNotNull(lsb2_3);
		assertNotNull(lsb2_3.getLastblock());
		assertEquals(2, lsb2_3.getTransactions().size());
		assertNotEquals(lsb2_2.getLastblock(), lsb2_3.getLastblock());

		txid = wallet2.sendtoaddress(address1_1, new BigDecimal("1.00"));

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();

		TransactionsSinceBlock lsb1_4 = wallet1.listsinceblock();
		assertNotNull(lsb1_4);
		assertNotNull(lsb1_4.getLastblock());
		assertEquals(2, lsb1_4.getTransactions().size());
		assertNotEquals(lsb1_3.getLastblock(), lsb1_4.getLastblock());

		TransactionsSinceBlock lsb2_4 = wallet2.listsinceblock();
		assertNotNull(lsb2_4);
		assertNotNull(lsb2_4.getLastblock());
		assertEquals(3, lsb2_4.getTransactions().size());
		assertNotEquals(lsb2_3.getLastblock(), lsb2_4.getLastblock());

		MultiTransaction t = wallet2.gettransaction(txid);

		TransactionsSinceBlock lsb1_5 = wallet1.listsinceblock(t.getBlockhash());
		assertNotNull(lsb1_5);
		assertNotNull(lsb1_5.getLastblock());
		assertEquals(1, lsb1_5.getTransactions().size());
		assertEquals(lsb1_4.getLastblock(), lsb1_5.getLastblock());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();

		Block lb1_5 = wallet1.getblock(lsb1_5.getLastblock());

		TransactionsSinceBlock lsb1_6 = wallet1.listsinceblock(t.getBlockhash(), lb1_5.getConfirmations());
		assertNotNull(lsb1_6);
		assertNotNull(lsb1_6.getLastblock());
		assertEquals(1, lsb1_6.getTransactions().size());
		assertEquals(lsb1_5.getLastblock(), lsb1_6.getLastblock());

		TransactionsSinceBlock lsb1_7 = wallet1.listsinceblock(t.getBlockhash(), lb1_5.getConfirmations() - 1);
		assertNotNull(lsb1_7);
		assertNotNull(lsb1_7.getLastblock());
		assertEquals(1, lsb1_7.getTransactions().size());
		assertNotEquals(lsb1_5.getLastblock(), lsb1_7.getLastblock());
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
