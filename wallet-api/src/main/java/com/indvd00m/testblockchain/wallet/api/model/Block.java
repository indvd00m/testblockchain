package com.indvd00m.testblockchain.wallet.api.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class Block {

	private String hash;
	private int confirmations;
	private int size;
	private int height;
	private int version;
	private String merkleroot;
	private List<String> tx;
	private long time;
	private long nonce;
	private String bits;
	private BigDecimal difficulty;
	private String previousblockhash;
	private String chainwork;
	private String nextblockhash;
	private long mediantime;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(int confirmations) {
		this.confirmations = confirmations;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getMerkleroot() {
		return merkleroot;
	}

	public void setMerkleroot(String merkleroot) {
		this.merkleroot = merkleroot;
	}

	public List<String> getTx() {
		return tx;
	}

	public void setTx(List<String> tx) {
		this.tx = tx;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String getBits() {
		return bits;
	}

	public void setBits(String bits) {
		this.bits = bits;
	}

	public BigDecimal getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(BigDecimal difficulty) {
		this.difficulty = difficulty;
	}

	public String getPreviousblockhash() {
		return previousblockhash;
	}

	public void setPreviousblockhash(String previousblockhash) {
		this.previousblockhash = previousblockhash;
	}

	public String getNextblockhash() {
		return nextblockhash;
	}

	public void setNextblockhash(String nextblockhash) {
		this.nextblockhash = nextblockhash;
	}

	public String getChainwork() {
		return chainwork;
	}

	public void setChainwork(String chainwork) {
		this.chainwork = chainwork;
	}

	public long getMediantime() {
		return mediantime;
	}

	public void setMediantime(long mediantime) {
		this.mediantime = mediantime;
	}
}
