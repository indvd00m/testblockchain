# TestBlockchain

TestBlockchain is a cryptocurrency wallet imitation. This wallet is independent from any real 
blockchain, does not support any kind of decentralization and cryptography, and implement only
part of original RPC methods. One of purposes of this project is a testing of some kind of 
integrations with real cryptocurrency wallets. 

TestBlockchain is allows you imitate real blockchain with any number of wallets. You can set 
start balance of this wallets and set time period for blocks generation. Then you can connect 
to this wallets over JSON-RPC protocol (in exactly the same way as you would connect to real 
wallets) and create transactions, send coins, wait confirmations, etc. No any data will be 
persisted, so after stopping of TestBlockchain and starting again new blockchain would be 
created.

## Requirements
TestBlockchain require java 8 for running.

## Using

### Command-line interface
// TODO

### Java configuration

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
Create wallets and start blokchain:

```java
		// TODO
```

## CI
Travis builds: 
[![Build Status](https://travis-ci.org/indvd00m/testblockchain.svg?branch=master)](https://travis-ci.org/indvd00m/testblockchain)

## Implementation details

### Supported JSON-RPC methods:
// TODO



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
