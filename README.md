# TestBlockchain

TestBlockchain is a cryptocurrency wallet imitation. This wallet is independent from any real blockchain, does not support any kind of decentralization and cryptography, and implement only part of original [RPC methods](https://bitcoin.org/en/developer-reference#rpcs). One of purposes of this project is a testing of some kind of integrations with real cryptocurrency wallets. 

TestBlockchain is allows you imitate real blockchain with any number of wallets. You can set start balance of this wallets and set time period for blocks generation. Then you can connect to this wallets over JSON-RPC protocol (in exactly the same way as you would [connect](https://bitcoin.org/en/developer-reference#remote-procedure-calls-rpcs) to real wallets) and create transactions, send coins, wait confirmations, etc. No any data will be persisted or sent somewhere, so after stopping of TestBlockchain and starting again new blockchain would be created.

## Requirements
TestBlockchain require java 8 for running.

## Using

### Command-line
Download and unzip `testblockchain-cli` zip archive. Start blockchain with name `BTC` and two
wallets at ports `10000` and `10001` and start balances `3` and `5.5` coins:

```bash
$ ./testblockchain.sh --name BTC --wallet 10000:3 --wallet 10001:5.5
```

Create some transactions, for example by original [Bitcoin Core](https://bitcoin.org/en/download) client.

```bash
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10000 getbalance
3
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10001 getbalance
5.5
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10000 getnewaddress
a91f98f6c36e80b35cf620beed7be788
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10001 sendtoaddress a91f98f6c36e80b35cf620beed7be788 1.005
606b6f9ed61133036fa967ddf7e3ff610110ba7a03627c2a521bd95e0951900a
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10000 getbalance
4.005
$ ./bitcoin-cli -rpcconnect=localhost -rpcport=10001 getbalance
4.495

```

Available `testblockchain` CLI options:

| Name | Long name | Required | Default value | Description |
| --- | --- | --- | --- | --- |
| `-n` | `--name` | Yes | | Coin name. |
| `-w` | `--wallet` | Yes | | Wallet definition in `<port:balance>` format. Specify local TCP port and starting balance of wallet. This option can be used multiple times. |
| `-bp` | `--block-period` | No | 1000 | Block generation period in millis. |
| `-v` | `--version` | No | | Print version and exit. |
| `-h` | `--help` | No | | Print help and exit. |

### Java

Add repository to your POM:

```xml
	<repository>
		<id>indvd00m-github-repo</id>
		<url>https://github.com/indvd00m/maven-repo/raw/master/repository</url>
	</repository>
```

Add dependency to your maven project:

```xml
	<dependency>
		<groupId>com.indvd00m.testblockchain</groupId>
		<artifactId>wallet-impl</artifactId>
		<version>1.0.4-SNAPSHOT</version>
	</dependency>
```
Create blockchain, wallets and some transactions:

```java
	@Test
	public void test() throws InterruptedException {
		Blockchain blockchain = new BlockchainImpl(1000);
		Wallet wallet1 = new WalletImpl(blockchain, new BigDecimal("3"));
		Wallet wallet2 = new WalletImpl(blockchain, new BigDecimal("5.5"));
		blockchain.start();

		waitNextBlock(blockchain);

		assertEquals(0, wallet1.getbalance().compareTo(new BigDecimal("3")));
		assertEquals(0, wallet2.getbalance().compareTo(new BigDecimal("5.5")));

		String address1_1 = wallet1.getnewaddress();
		wallet2.sendtoaddress(address1_1, new BigDecimal("1.005"));
		waitNextBlock(blockchain);

		assertEquals(0, wallet1.getbalance().compareTo(new BigDecimal("4.005")));
		assertEquals(0, wallet2.getbalance().compareTo(new BigDecimal("4.495")));
	}

	protected void waitNextBlock(Blockchain blockchain) throws InterruptedException {
		int confirmations = blockchain.getConfirmations();
		while (confirmations == blockchain.getConfirmations()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}
```

See tests in module `wallet-impl` for more examples. For remote connecting over JSON-RPC protocol with [jsonrpc4j](https://github.com/briandilley/jsonrpc4j) see example in test `TestCoinServer`.

## Supported JSON-RPC methods:
List of original [RPC methods](https://bitcoin.org/en/developer-reference#rpcs) which now implemented:

| Name | Notes |
| --- | --- |
| `getaccount` | |
| `getaccountaddress` | |
| `getaddressesbyaccount` | |
| `getbalance` | |
| `getblock` | |
| `getblockcount` | |
| `getblockhash` | |
| `getinfo` | |
| `getnewaddress` | |
| `getreceivedbyaccount` | |
| `getreceivedbyaddress` | |
| `gettransaction` | |
| `listaccounts` | |
| `listreceivedbyaccount` | |
| `listreceivedbyaddress` | |
| `listsinceblock` | |
| `listtransactions` | |
| `sendtoaddress` | |
| `settxfee` | |
| `validateaddress` | |

Other methods will return `-32001` or `-32601` errors codes. Behavior of some implemented methods may differ from original blockchain but as far as possible it repeats the original.


## CI
Travis builds: 
[![Build Status](https://travis-ci.org/indvd00m/testblockchain.svg?branch=master)](https://travis-ci.org/indvd00m/testblockchain)

## Implementation details


## Download release

https://github.com/indvd00m/testblockchain/releases

## Roadmap

This project is developed as a hobby with no public roadmap or any guarantees of upcoming releases.

## Issue tracking

The issues for this project are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Building and running tests
```bash
git clone https://github.com/indvd00m/testblockchain/
cd testblockchain
mvn clean install
```

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

TestBlockchain is distributed under Apache License 2.0. For license terms, see LICENSE.

TestBlockchain is written by David E. Veliev.
