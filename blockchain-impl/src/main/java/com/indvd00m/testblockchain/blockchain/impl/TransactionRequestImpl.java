package com.indvd00m.testblockchain.blockchain.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.indvd00m.testblockchain.blockchain.api.TransactionRequest;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TransactionRequestImpl implements TransactionRequest {

	String txid;
	Map<String, BigDecimal> fromAddresses = new HashMap<>();
	Map<String, BigDecimal> toAddresses = new HashMap<>();
	BigDecimal amount;
	BigDecimal fee;
	Date date;

	public TransactionRequestImpl(String txid, Map<String, BigDecimal> fromAddresses,
			Map<String, BigDecimal> toAddresses, BigDecimal amount, BigDecimal fee, Date date) {
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

}
