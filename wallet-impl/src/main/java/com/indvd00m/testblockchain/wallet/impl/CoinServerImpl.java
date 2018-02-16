package com.indvd00m.testblockchain.wallet.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.blockchain.impl.BlockchainImpl;
import com.indvd00m.testblockchain.wallet.api.CoinServer;
import com.indvd00m.testblockchain.wallet.api.Wallet;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class CoinServerImpl implements CoinServer {

	String coinName;
	Map<Integer, BigDecimal> walletBalancesByPort;
	long blockGenerationPeriodMillis = 1000;
	int scale = BlockchainImpl.DEFAULT_SCALE;

	// data
	Blockchain blockchain;
	Map<Integer, JsonRpcServer> serversByPort;
	Server server;

	// locs
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	Logger log;

	public CoinServerImpl(String coinName, Map<Integer, BigDecimal> walletBalancesByPort) {
		if (walletBalancesByPort.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.coinName = coinName;
		this.walletBalancesByPort = new HashMap<>(walletBalancesByPort);
		log = LoggerFactory.getLogger(coinName);
	}

	public CoinServerImpl(String coinName, Map<Integer, BigDecimal> walletBalancesByPort,
			long blockGenerationPeriodMillis) {
		this(coinName, walletBalancesByPort);
		this.blockGenerationPeriodMillis = blockGenerationPeriodMillis;
	}

	public CoinServerImpl(String coinName, Map<Integer, BigDecimal> walletBalancesByPort,
			long blockGenerationPeriodMillis, int scale) {
		this(coinName, walletBalancesByPort, blockGenerationPeriodMillis);
		this.scale = scale;
	}

	@Override
	public void start() throws Exception {
		w.lock();
		try {
			// rpc server
			log.info(String.format("Prepare %d JSON-RPC servers", walletBalancesByPort.size()));

			blockchain = new BlockchainImpl(blockGenerationPeriodMillis, scale);
			serversByPort = new HashMap<>();
			walletBalancesByPort.entrySet().forEach(e -> {
				Integer port = e.getKey();
				BigDecimal startBalance = e.getValue();
				log.info(String.format("Creating JSON-RPC server at port %d with start balance %s", port,
						startBalance.toPlainString()));
				Wallet wallet = new WalletImpl(blockchain, startBalance);
				JsonRpcServer rpcServer = new JsonRpcServer(new ObjectMapper(), wallet, Wallet.class);
				serversByPort.put(port, rpcServer);
			});

			// http server
			log.info(String.format("Creating http server"));
			QueuedThreadPool threadPool = new QueuedThreadPool();
			server = new Server(threadPool);

			List<ServerConnector> connectors = new ArrayList<>();
			walletBalancesByPort.entrySet().forEach(e -> {
				ServerConnector connector = new ServerConnector(server);
				Integer port = e.getKey();
				connector.setPort(port);
				connectors.add(connector);
			});
			server.setConnectors(connectors.toArray(new ServerConnector[connectors.size()]));

			ServletContextHandler handler = new ServletContextHandler();
			server.setHandler(handler);

			@SuppressWarnings("serial")
			ServletHolder rootHolder = new ServletHolder(new HttpServlet() {

				@Override
				protected void service(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException {
					JsonRpcServer rpcServer;
					r.lock();
					try {
						int port = req.getLocalPort();
						rpcServer = serversByPort.get(port);
					} finally {
						r.unlock();
					}
					rpcServer.handle(req, resp);
				}

			});
			handler.addServlet(rootHolder, "/");

			blockchain.start();
			server.start();

			log.info(String.format("JSON-RPC server started!"));
		} catch (Exception e) {
			log.error("ERROR", e);
			if (server != null) {
				server.stop();
			}
			if (blockchain != null) {
				blockchain.stop();
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	public void stop() throws Exception {
		w.lock();
		try {
			server.stop();
			blockchain.stop();

			log.info(String.format("%s JSON-RPC server stopped!", coinName));
		} catch (Exception e) {
			log.error("ERROR", e);
			if (server != null) {
				try {
					server.stop();
				} catch (Exception e1) {
					log.error("ERROR", e1);
				}
			}
			if (blockchain != null) {
				try {
					blockchain.stop();
				} catch (Exception e1) {
					log.error("ERROR", e1);
				}
			}
		} finally {
			w.unlock();
		}
	}

	@Override
	public String getCoinName() {
		return coinName;
	}

	@Override
	public Map<Integer, BigDecimal> getWalletBalancesByPort() {
		return Collections.unmodifiableMap(new HashMap<>(walletBalancesByPort));
	}

	@Override
	public long getBlockGenerationPeriodMillis() {
		return blockGenerationPeriodMillis;
	}

	@Override
	public int getScale() {
		return scale;
	}

}
