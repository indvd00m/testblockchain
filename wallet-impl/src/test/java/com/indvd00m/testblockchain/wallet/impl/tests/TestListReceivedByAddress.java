package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.api.model.ReceivedByAddress;
import com.indvd00m.testblockchain.wallet.impl.WalletImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestListReceivedByAddress {

	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		blockchain.start();

		Wallet wallet1 = new WalletImpl(blockchain);
		Wallet wallet2 = new WalletImpl(blockchain, new BigDecimal("10"));
		waitBlockchainNextBlock(blockchain);

		String address1_1 = wallet1.getnewaddress();
		String txid1 = wallet2.sendtoaddress(address1_1, new BigDecimal("1.00"));
		waitBlockchainNextBlock(blockchain);

		String address1_2 = wallet1.getnewaddress();
		String txid2 = wallet2.sendtoaddress(address1_2, new BigDecimal("2.00"));
		waitBlockchainNextBlock(blockchain);

		String txid3 = wallet2.sendtoaddress(address1_2, new BigDecimal("3.00"));
		waitBlockchainNextBlock(blockchain);

		blockchain.stop();
		waitBlockchainStop(blockchain);

		List<ReceivedByAddress> list1 = wallet1.listreceivedbyaddress();
		Map<String, ReceivedByAddress> listByAddress1 = list1.stream()
				.collect(Collectors.toMap(r -> r.getAddress(), r -> r));
		assertTrue(listByAddress1.containsKey(address1_1));
		assertTrue(listByAddress1.containsKey(address1_2));
		ReceivedByAddress received1_1_1 = listByAddress1.get(address1_1);
		ReceivedByAddress received1_2_1 = listByAddress1.get(address1_2);
		assertTrue(received1_1_1.getTxids().contains(txid1));
		assertFalse(received1_1_1.getTxids().contains(txid2));
		assertFalse(received1_1_1.getTxids().contains(txid3));
		assertFalse(received1_2_1.getTxids().contains(txid1));
		assertTrue(received1_2_1.getTxids().contains(txid2));
		assertTrue(received1_2_1.getTxids().contains(txid3));
		assertTrue(received1_1_1.getConfirmations() > received1_2_1.getConfirmations());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();
		waitBlockchainStop(blockchain);

		List<ReceivedByAddress> list2 = wallet1.listreceivedbyaddress();
		Map<String, ReceivedByAddress> listByAddress2 = list2.stream()
				.collect(Collectors.toMap(r -> r.getAddress(), r -> r));
		assertTrue(listByAddress2.containsKey(address1_1));
		assertTrue(listByAddress2.containsKey(address1_2));
		ReceivedByAddress received1_1_2 = listByAddress2.get(address1_1);
		ReceivedByAddress received1_2_2 = listByAddress2.get(address1_2);
		assertTrue(received1_1_2.getTxids().contains(txid1));
		assertFalse(received1_1_2.getTxids().contains(txid2));
		assertFalse(received1_1_2.getTxids().contains(txid3));
		assertFalse(received1_2_2.getTxids().contains(txid1));
		assertTrue(received1_2_2.getTxids().contains(txid2));
		assertTrue(received1_2_2.getTxids().contains(txid3));
		assertTrue(received1_1_2.getConfirmations() > received1_2_2.getConfirmations());
		assertTrue(received1_1_2.getConfirmations() > received1_1_1.getConfirmations());
		assertTrue(received1_2_2.getConfirmations() > received1_2_1.getConfirmations());

		blockchain.start();
		waitBlockchainNextBlock(blockchain);
		blockchain.stop();
		waitBlockchainStop(blockchain);

		List<ReceivedByAddress> list3 = wallet1.listreceivedbyaddress();
		Map<String, ReceivedByAddress> listByAddress3 = list3.stream()
				.collect(Collectors.toMap(r -> r.getAddress(), r -> r));
		assertTrue(listByAddress3.containsKey(address1_1));
		assertTrue(listByAddress3.containsKey(address1_2));
		ReceivedByAddress received1_1_3 = listByAddress3.get(address1_1);
		ReceivedByAddress received1_2_3 = listByAddress3.get(address1_2);
		assertTrue(received1_1_3.getTxids().contains(txid1));
		assertFalse(received1_1_3.getTxids().contains(txid2));
		assertFalse(received1_1_3.getTxids().contains(txid3));
		assertFalse(received1_2_3.getTxids().contains(txid1));
		assertTrue(received1_2_3.getTxids().contains(txid2));
		assertTrue(received1_2_3.getTxids().contains(txid3));
		assertTrue(received1_1_3.getConfirmations() > received1_2_3.getConfirmations());
		assertTrue(received1_1_3.getConfirmations() > received1_1_2.getConfirmations());
		assertTrue(received1_2_3.getConfirmations() > received1_2_2.getConfirmations());

		{
			List<ReceivedByAddress> list3_prev = wallet1.listreceivedbyaddress(received1_1_3.getConfirmations() - 1);
			ReceivedByAddress received1_1_3_prev = list3_prev.stream()
					.filter(r -> r.getAddress().equals(received1_1_3.getAddress())).findFirst().orElse(null);
			assertNotNull(received1_1_3_prev);
			assertEqualsComparable(received1_1_3.getConfirmations(), received1_1_3_prev.getConfirmations());
		}

		{
			List<ReceivedByAddress> list3_prev = wallet1.listreceivedbyaddress(received1_2_3.getConfirmations() - 1);
			ReceivedByAddress received1_2_3_prev = list3_prev.stream()
					.filter(r -> r.getAddress().equals(received1_2_3.getAddress())).findFirst().orElse(null);
			assertNotNull(received1_2_3_prev);
			assertEqualsComparable(received1_2_3.getConfirmations(), received1_2_3_prev.getConfirmations());
		}

		{
			List<ReceivedByAddress> list3_next = wallet1.listreceivedbyaddress(received1_1_3.getConfirmations() + 1);
			ReceivedByAddress received1_1_3_next = list3_next.stream()
					.filter(r -> r.getAddress().equals(received1_1_3.getAddress())).findFirst().orElse(null);
			assertNull(received1_1_3_next);
		}

		{
			List<ReceivedByAddress> list3_next = wallet1.listreceivedbyaddress(received1_1_3.getConfirmations() + 1,
					true);
			ReceivedByAddress received1_1_3_next = list3_next.stream()
					.filter(r -> r.getAddress().equals(received1_1_3.getAddress())).findFirst().orElse(null);
			assertNotNull(received1_1_3_next);
			assertEquals(0, received1_1_3_next.getConfirmations());
			assertEqualsComparable(BigDecimal.ZERO, received1_1_3_next.getAmount());
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

	protected <T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
		}
	}

}
