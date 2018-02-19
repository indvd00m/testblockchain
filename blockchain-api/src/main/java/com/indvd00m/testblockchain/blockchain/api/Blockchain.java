package com.indvd00m.testblockchain.blockchain.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public interface Blockchain {

	int DEFAULT_SCALE = 8;

	boolean isStarted();

	void start();

	void stop();

	String addTransactionRequest(String fromAddress, String toAddress, BigDecimal amount) throws TransactionException;

	String addTransactionRequest(String fromAddress, String toAddress, BigDecimal amount, BigDecimal fee)
			throws TransactionException;

	String addTransactionRequest(Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount) throws TransactionException;

	String addTransactionRequest(Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount, BigDecimal fee) throws TransactionException;

	String generateAddress();

	int getConfirmations();

	List<Block> getChain();

	List<Block> getChain(int confirmations);

	BigDecimal getBalance(String address);

	BigDecimal getBalance(Collection<String> addresses);

	BigDecimal getBalance(String address, int confirmations);

	BigDecimal getBalance(Collection<String> addresses, int confirmations);

	BigDecimal getReceived(String address);

	BigDecimal getReceived(Collection<String> addresses);

	BigDecimal getReceived(String address, int confirmations);

	BigDecimal getReceived(Collection<String> addresses, int confirmations);

	Transaction getLastTransaction(String address);

	Transaction getLastTransaction(String address, int confirmations);

	int getConfirmations(String blockHash);

	Block getBlock(String blockHash);

	Block getBlock(int index);

	Transaction getTransaction(String txid);

	int getVersion();

	boolean isValid(String address);

	int getScale();

}