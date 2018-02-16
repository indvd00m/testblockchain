package com.indvd00m.testblockchain.blockchain.api;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
@SuppressWarnings("serial")
public class TransactionException extends Exception {

	public TransactionException() {
		super();
	}

	public TransactionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
