package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.math.BigDecimal;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class TransactionDetail {
	private String account;
	private String address;
	private TransactionCategory category;
	private BigDecimal amount;
	private BigDecimal fee;
	private BigDecimal vout;
	private String label;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getVout() {
		return vout;
	}

	public void setVout(BigDecimal vout) {
		this.vout = vout;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
