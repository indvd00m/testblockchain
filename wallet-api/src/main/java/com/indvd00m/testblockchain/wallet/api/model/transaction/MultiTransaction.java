package com.indvd00m.testblockchain.wallet.api.model.transaction;

import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public class MultiTransaction extends Transaction {
	private List<TransactionDetail> details;

	public List<TransactionDetail> getDetails() {
		return details;
	}

	public void setDetails(List<TransactionDetail> details) {
		this.details = details;
	}

}
