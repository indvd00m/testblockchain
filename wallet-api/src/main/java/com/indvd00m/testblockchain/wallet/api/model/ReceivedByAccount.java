package com.indvd00m.testblockchain.wallet.api.model;

import java.math.BigDecimal;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class ReceivedByAccount {
	private String account;
	private BigDecimal amount;
	private long confirmations;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(long confirmations) {
		this.confirmations = confirmations;
	}
}
