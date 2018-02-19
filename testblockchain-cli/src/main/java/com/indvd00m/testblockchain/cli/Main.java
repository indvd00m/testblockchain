package com.indvd00m.testblockchain.cli;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import com.indvd00m.testblockchain.blockchain.api.Blockchain;
import com.indvd00m.testblockchain.wallet.api.CoinServer;
import com.indvd00m.testblockchain.wallet.impl.CoinServerImpl;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class Main {

	public static final String PROGRAMM_NAME = "testblockchain";
	public static final String WALLET_BALANCE_BY_PORT_PATTERN = "(\\d+):((?:\\d*\\.)?\\d+)";

	public static void main(String[] args) throws Exception {
		Configurations configs = new Configurations();
		Configuration config = configs.properties(Main.class.getClassLoader().getResource("config.properties"));

		Option helpOption = Option.builder("h").longOpt("help").hasArg(false).desc("show options description and exit")
				.build();
		Option versionOption = Option.builder("v").longOpt("version").hasArg(false).desc("print version and exit")
				.build();

		Options infoOptions = new Options();
		infoOptions.addOption(helpOption);
		infoOptions.addOption(versionOption);

		Option coinNameOption = Option.builder("n").longOpt("name").argName("name").hasArg().required()
				.desc("coin name").build();
		Option walletOption = Option.builder("w").longOpt("wallet").argName("port:balance").hasArg().required().desc(
				"Wallet definition. Specify local TCP <port> and starting <balance> of wallet. This option can be used multiple times.")
				.build();
		Option blockPeriodOption = Option.builder("bp").longOpt("block-period").argName("millis").hasArg()
				.type(Number.class).desc(String.format("block generation period in millis, %d by default",
						CoinServer.DEFAULT_BLOCK_GENERATION_PERIOD_MILLIS))
				.build();

		Options startOptions = new Options();
		startOptions.addOption(coinNameOption);
		startOptions.addOption(walletOption);
		startOptions.addOption(blockPeriodOption);

		try {
			CommandLineParser parser = new DefaultParser();

			CommandLine cmd = parser.parse(infoOptions, args, true);
			if (cmd.hasOption(helpOption.getLongOpt())) {
				println("TestBlockchain - cryptocurrency wallet implementation for tests.");
				Options options = new Options();
				startOptions.getOptions().stream().forEachOrdered(o -> options.addOption(o));
				infoOptions.getOptions().stream().forEachOrdered(o -> options.addOption(o));
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(PROGRAMM_NAME, options);
				return;
			}
			if (cmd.hasOption(versionOption.getLongOpt())) {
				String version = config.getString("version");
				println("%s version %s", PROGRAMM_NAME, version);
				return;
			}

			cmd = parser.parse(startOptions, args, false);
			String coinName = cmd.getOptionValue(coinNameOption.getLongOpt());

			String[] walletInfos = cmd.getOptionValues(walletOption.getLongOpt());
			Map<Integer, BigDecimal> walletBalancesByPort = new HashMap<>();
			Pattern pattern = Pattern.compile(WALLET_BALANCE_BY_PORT_PATTERN);
			for (String walletInfo : walletInfos) {
				Matcher m = pattern.matcher(walletInfo);
				if (!m.matches()) {
					throw new ParseException(String.format("%s option do not match pattern: <port>:<balance>",
							walletOption.getLongOpt()));
				}
				Integer port = Integer.valueOf(m.group(1));
				BigDecimal balance = new BigDecimal(m.group(2));
				balance = fixScale(balance, Blockchain.DEFAULT_SCALE);
				walletBalancesByPort.put(port, balance);
			}

			Long blockPeriod = null;
			if (cmd.hasOption(blockPeriodOption.getLongOpt())) {
				blockPeriod = (Long) cmd.getParsedOptionValue(blockPeriodOption.getLongOpt());
			}

			CoinServer server = null;
			if (blockPeriod == null) {
				server = new CoinServerImpl(coinName, walletBalancesByPort);
			} else {
				server = new CoinServerImpl(coinName, walletBalancesByPort, blockPeriod.longValue());
			}
			server.start();
		} catch (ParseException e) {
			println(e.getMessage());
			println("Use --%s option for more information", helpOption.getLongOpt());
		}
	}

	private static BigDecimal fixScale(BigDecimal value, int scale) {
		BigDecimal scaledValue = value.setScale(scale, RoundingMode.HALF_DOWN);
		if (value.compareTo(scaledValue) != 0) {
			// ok, pass illegal value to coin server
			return value;
		} else {
			return scaledValue;
		}
	}

	private static void println(String s) {
		System.out.println(s);
	}

	private static void println(String s, Object... args) {
		System.out.println(String.format(s, args));
	}

}
