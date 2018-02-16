package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.math.BigDecimal;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class Transaction extends AbstractTransaction {
	private String account;
	private String address;
	private TransactionCategory category;
	private String otheraccount;
	private String comment;
	private String to;
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

	public String getOtheraccount() {
		return otheraccount;
	}

	public void setOtheraccount(String otheraccount) {
		this.otheraccount = otheraccount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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
