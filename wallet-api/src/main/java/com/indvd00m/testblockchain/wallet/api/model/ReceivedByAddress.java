package com.indvd00m.testblockchain.wallet.api.model;

import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class ReceivedByAddress extends ReceivedByAccount {
	private String address;
	private List<String> txids;
	private String label;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getTxids() {
		return txids;
	}

	public void setTxids(List<String> txids) {
		this.txids = txids;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
