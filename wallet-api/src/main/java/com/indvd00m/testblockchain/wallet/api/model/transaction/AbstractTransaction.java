package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class AbstractTransaction {
	private BigDecimal amount;
	private BigDecimal fee;
	private long confirmations;
	private String blockhash;
	private long blockindex;
	private long blocktime;
	private String txid;
	private long time;
	private long timereceived;
	private List<String> walletconflicts;
	private String hex;

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

	public long getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(long confirmations) {
		this.confirmations = confirmations;
	}

	public String getBlockhash() {
		return blockhash;
	}

	public void setBlockhash(String blockhash) {
		this.blockhash = blockhash;
	}

	public long getBlockindex() {
		return blockindex;
	}

	public void setBlockindex(long blockindex) {
		this.blockindex = blockindex;
	}

	public long getBlocktime() {
		return blocktime;
	}

	public void setBlocktime(long blocktime) {
		this.blocktime = blocktime;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTimereceived() {
		return timereceived;
	}

	public void setTimereceived(long timereceived) {
		this.timereceived = timereceived;
	}

	public List<String> getWalletconflicts() {
		return walletconflicts;
	}

	public void setWalletconflicts(List<String> walletconflicts) {
		this.walletconflicts = walletconflicts;
	}

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

}
