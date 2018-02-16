package com.indvd00m.testblockchain.blockchain.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.indvd00m.testblockchain.blockchain.api.Block;
import com.indvd00m.testblockchain.blockchain.api.Transaction;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TransactionImpl implements Transaction {

	String txid;
	Map<String, BigDecimal> fromAddresses = new HashMap<>();
	Map<String, BigDecimal> toAddresses = new HashMap<>();
	BigDecimal amount;
	BigDecimal fee;
	Date date;
	Date receivedDate;
	Block block;

	public TransactionImpl(String txid, Map<String, BigDecimal> fromAddresses, Map<String, BigDecimal> toAddresses,
			BigDecimal amount, BigDecimal fee, Date date, Date receivedDate) {
		super();
		this.txid = txid;
		if (fromAddresses != null) {
			this.fromAddresses.putAll(fromAddresses);
		}
		if (toAddresses != null) {
			this.toAddresses.putAll(toAddresses);
		}
		this.amount = amount;
		this.fee = fee;
		this.date = date;
		this.receivedDate = receivedDate;
	}

	@Override
	public String getTxid() {
		return txid;
	}

	@Override
	public Map<String, BigDecimal> getFromAddresses() {
		return Collections.unmodifiableMap(fromAddresses);
	}

	@Override
	public Map<String, BigDecimal> getToAddresses() {
		return Collections.unmodifiableMap(toAddresses);
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public BigDecimal getFee() {
		return fee;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Date getReceivedDate() {
		return receivedDate;
	}

	@Override
	public Block getBlock() {
		return block;
	}

}
