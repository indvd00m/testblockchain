package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.math.BigDecimal;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class UnspentTransaction {
	private String txid;
	private BigDecimal vout;
	private String address;
	private String account;
	private String scriptPubKey;
	private BigDecimal amount;
	private long confirmations;
	private boolean spendable;

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public BigDecimal getVout() {
		return vout;
	}

	public void setVout(BigDecimal vout) {
		this.vout = vout;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getScriptPubKey() {
		return scriptPubKey;
	}

	public void setScriptPubKey(String scriptPubKey) {
		this.scriptPubKey = scriptPubKey;
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

	public boolean isSpendable() {
		return spendable;
	}

	public void setSpendable(boolean spendable) {
		this.spendable = spendable;
	}

}
