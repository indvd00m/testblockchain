#!/bin/sh

for var in ./lib/*; do
	classpath=$classpath:${var}
done

java -classpath "$classpath" com.indvd00m.testblockchain.cli.Main $*
