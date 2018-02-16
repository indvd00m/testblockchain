package com.indvd00m.testblockchain.blockchain.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.indvd00m.testblockchain.blockchain.api.Block;
import com.indvd00m.testblockchain.blockchain.api.Transaction;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class BlockImpl implements Block {

	int index;
	String hash;
	Date date;
	Block previousBlock;
	List<Transaction> transactions = new ArrayList<>();

	public BlockImpl(int index, String hash, Date date, Block previousBlock, List<Transaction> transactions) {
		super();
		this.index = index;
		this.hash = hash;
		this.date = date;
		this.previousBlock = previousBlock;
		this.transactions = transactions;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Block getPreviousBlock() {
		return previousBlock;
	}

	@Override
	public List<Transaction> getTransactions() {
		return transactions;
	}

}
