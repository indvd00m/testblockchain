package com.indvd00m.testblockchain.wallet.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.indvd00m.testblockchain.wallet.api.CoinServer;
import com.indvd00m.testblockchain.wallet.api.Wallet;
import com.indvd00m.testblockchain.wallet.impl.CoinServerImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestCoinServer {

	@Test
	public void test() throws Exception {
		String coinName = "BTC";

		// TODO fix ports allocation
		Map<Integer, BigDecimal> walletBalancesByPort = new HashMap<>();
		walletBalancesByPort.put(getAvailablePort(), new BigDecimal("1.00"));
		walletBalancesByPort.put(getAvailablePort(), new BigDecimal("2.00"));
		walletBalancesByPort.put(getAvailablePort(), new BigDecimal("3.00"));
		walletBalancesByPort.put(getAvailablePort(), new BigDecimal("4.00"));
		walletBalancesByPort.put(getAvailablePort(), new BigDecimal("5.00"));

		long blockGenerationPeriodMillis = 1000;
		CoinServer server = new CoinServerImpl(coinName, walletBalancesByPort, blockGenerationPeriodMillis);
		server.start();

		walletBalancesByPort.entrySet().stream().forEach(e -> {
			Integer port = e.getKey();
			assertTrue(isPortUsed(port));
		});

		Map<Integer, Wallet> servicesByPort = new HashMap<>();
		walletBalancesByPort.entrySet().stream().forEach(e -> {
			int port = e.getKey();
			String rpcUrl = String.format("http://localhost:%d", port);
			Wallet wallet = getWallet(rpcUrl);
			servicesByPort.put(port, wallet);
		});

		walletBalancesByPort.entrySet().stream().forEach(e -> {
			Integer port = e.getKey();
			BigDecimal balance = e.getValue();

			Wallet wallet = servicesByPort.get(port);
			BigDecimal serviceBalance = wallet.getbalance();
			assertEqualsComparable(balance, serviceBalance);
		});

		List<Integer> ports = new ArrayList<>(walletBalancesByPort.keySet());

		{
			int port1 = ports.get(0);
			Wallet wallet1 = servicesByPort.get(port1);
			BigDecimal balance1 = wallet1.getbalance();
			BigDecimal fee1 = wallet1.getinfo().getPaytxfee();

			int port2 = ports.get(ports.size() - 1);
			Wallet wallet2 = servicesByPort.get(port2);
			BigDecimal balance2 = wallet2.getbalance();

			String address2 = wallet2.getnewaddress();

			BigDecimal amount = balance1.subtract(fee1).divide(new BigDecimal("2"), 8, RoundingMode.HALF_UP);
			String txid = wallet1.sendtoaddress(address2, amount);

			long prevConf = wallet1.gettransaction(txid).getConfirmations();
			while (wallet1.gettransaction(txid).getConfirmations() == prevConf) {
				TimeUnit.MILLISECONDS.sleep(100);
			}

			BigDecimal newBalance1 = balance1.subtract(fee1).subtract(amount);
			BigDecimal newBalance2 = balance2.add(amount);

			assertEqualsComparable(newBalance1, wallet1.getbalance());
			assertEqualsComparable(newBalance2, wallet2.getbalance());
		}
	}

	int getAvailablePort() throws IOException {
		try (ServerSocket s = new ServerSocket(0)) {
			int port = s.getLocalPort();
			return port;
		}
	}

	boolean isPortUsed(int port) {
		try (ServerSocket s = new ServerSocket(port)) {
			return false;
		} catch (IOException e) {
			return true;
		}
	}

	Wallet getWallet(String rpcUrl) {
		return getWallet(rpcUrl, new HashMap<String, String>());
	}

	Wallet getWallet(String rpcUrl, String username, String password) {
		HashMap<String, String> headers = new HashMap<String, String>();

		String authString = username + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		String paramValue = "Basic " + authStringEnc;
		headers.put("Authorization", paramValue);

		return getWallet(rpcUrl, headers);
	}

	Wallet getWallet(String rpcUrl, Map<String, String> headers) {
		try {
			ObjectMapper config = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			JsonRpcHttpClient client = new JsonRpcHttpClient(config, new URL(rpcUrl), headers);
			Wallet wallet = ProxyUtil.createClientProxy(getClass().getClassLoader(), Wallet.class, client);
			return wallet;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	<T extends Comparable<T>> void assertEqualsComparable(T t1, T t2) {
		if (t1.compareTo(t2) != 0) {
			assertEquals(t1, t2);
		}
	}

}
