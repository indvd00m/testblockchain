package com.indvd00m.testblockchain.wallet.api;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public interface CoinServer {

	int DEFAULT_BLOCK_GENERATION_PERIOD_MILLIS = 1000;

	void start() throws Exception;

	void stop() throws Exception;

	int getScale();

	long getBlockGenerationPeriodMillis();

	Map<Integer, BigDecimal> getWalletBalancesByPort();

	String getCoinName();

}