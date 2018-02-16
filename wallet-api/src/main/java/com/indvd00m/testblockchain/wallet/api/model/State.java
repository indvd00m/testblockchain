package com.indvd00m.testblockchain.wallet.api.model;

import java.math.BigDecimal;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class State {

	private int version;
	private int protocolversion;
	private int walletversion;
	private BigDecimal balance;
	private int blocks;
	private int timeoffset;
	private int connections;
	private String proxy;
	private BigDecimal difficulty;
	private boolean testnet;
	private long keypoololdest;
	private int keypoolsize;
	private BigDecimal paytxfee;
	private int unlocked_until;
	private BigDecimal mininput;
	private String errors;
	private BigDecimal relayfee;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getProtocolversion() {
		return protocolversion;
	}

	public void setProtocolversion(int protocolversion) {
		this.protocolversion = protocolversion;
	}

	public int getWalletversion() {
		return walletversion;
	}

	public void setWalletversion(int walletversion) {
		this.walletversion = walletversion;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public int getBlocks() {
		return blocks;
	}

	public void setBlocks(int blocks) {
		this.blocks = blocks;
	}

	public int getTimeoffset() {
		return timeoffset;
	}

	public void setTimeoffset(int timeoffset) {
		this.timeoffset = timeoffset;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public BigDecimal getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(BigDecimal difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isTestnet() {
		return testnet;
	}

	public void setTestnet(boolean testnet) {
		this.testnet = testnet;
	}

	public long getKeypoololdest() {
		return keypoololdest;
	}

	public void setKeypoololdest(long keypoololdest) {
		this.keypoololdest = keypoololdest;
	}

	public int getKeypoolsize() {
		return keypoolsize;
	}

	public void setKeypoolsize(int keypoolsize) {
		this.keypoolsize = keypoolsize;
	}

	public BigDecimal getPaytxfee() {
		return paytxfee;
	}

	public void setPaytxfee(BigDecimal paytxfee) {
		this.paytxfee = paytxfee;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public int getUnlocked_until() {
		return unlocked_until;
	}

	public void setUnlocked_until(int unlocked_until) {
		this.unlocked_until = unlocked_until;
	}

	public BigDecimal getMininput() {
		return mininput;
	}

	public void setMininput(BigDecimal mininput) {
		this.mininput = mininput;
	}

	public BigDecimal getRelayfee() {
		return relayfee;
	}

	public void setRelayfee(BigDecimal relayfee) {
		this.relayfee = relayfee;
	}

}
