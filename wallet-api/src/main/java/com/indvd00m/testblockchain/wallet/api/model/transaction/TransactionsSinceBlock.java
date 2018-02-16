package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class TransactionsSinceBlock {

	private List<Transaction> transactions;
	private String lastblock;

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getLastblock() {
		return lastblock;
	}

	public void setLastblock(String lastblock) {
		this.lastblock = lastblock;
	}
}
