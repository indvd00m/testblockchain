package com.indvd00m.testblockchain.blockchain.api;

import java.util.Date;
import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public interface Block {

	int getIndex();

	String getHash();

	Date getDate();

	Block getPreviousBlock();

	List<Transaction> getTransactions();

}