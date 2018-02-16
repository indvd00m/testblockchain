package com.indvd00m.testblockchain.blockchain.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public interface Transaction {

	String getTxid();

	Map<String, BigDecimal> getFromAddresses();

	Map<String, BigDecimal> getToAddresses();

	BigDecimal getAmount();

	BigDecimal getFee();

	Date getDate();

	Date getReceivedDate();

	Block getBlock();

}