package com.indvd00m.testblockchain.wallet.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcParamsPassMode;
import com.indvd00m.testblockchain.wallet.api.model.AddressValidation;
import com.indvd00m.testblockchain.wallet.api.model.Block;
import com.indvd00m.testblockchain.wallet.api.model.ReceivedByAccount;
import com.indvd00m.testblockchain.wallet.api.model.ReceivedByAddress;
import com.indvd00m.testblockchain.wallet.api.model.State;
import com.indvd00m.testblockchain.wallet.api.model.transaction.MultiTransaction;
import com.indvd00m.testblockchain.wallet.api.model.transaction.Transaction;
import com.indvd00m.testblockchain.wallet.api.model.transaction.TransactionsSinceBlock;
import com.indvd00m.testblockchain.wallet.api.model.transaction.UnspentTransaction;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * 
 */
public interface Wallet {

	/**
	 * Returns the account associated with the given address.
	 * 
	 * @param coinaddress
	 * @return
	 */
	public String getaccount(String coinaddress);

	/**
	 * Returns the current coin address for receiving payments to this account.
	 * getaccountaddress() will return the same address until coins are received on
	 * that address; once coins have been received, it will generate and return a
	 * new address.
	 * 
	 * @param account
	 * @return
	 */
	public String getaccountaddress(String account);

	/**
	 * 
	 * Returns the list of addresses for the given account.
	 * 
	 * @param account
	 * @return
	 */
	public List<String> getaddressesbyaccount(String account);

	/**
	 * If [account] is not specified, returns the server's total available balance.
	 * If [account] is specified, returns the balance in the account. [minconf=1]
	 * 
	 * @return
	 */
	public BigDecimal getbalance();

	/**
	 * If [account] is not specified, returns the server's total available balance.
	 * If [account] is specified, returns the balance in the account. [minconf=1]
	 * 
	 * @param account
	 * @return
	 */
	public BigDecimal getbalance(String account);

	/**
	 * If [account] is not specified, returns the server's total available balance.
	 * If [account] is specified, returns the balance in the account.
	 * 
	 * @param account
	 * @param minconf
	 * @return
	 */
	public BigDecimal getbalance(String account, long minconf);

	/**
	 * Returns information about the block with the given hash.
	 * 
	 * @param hash
	 * @return
	 */
	public Block getblock(String hash);

	/**
	 * Returns the number of blocks in the longest block chain.
	 * 
	 * @return
	 */
	public long getblockcount();

	/**
	 * Returns hash of block in best-block-chain at index; index 0 is the genesis
	 * block.
	 * 
	 * @param index
	 * @return
	 */
	public String getblockhash(long index);

	/**
	 * Returns the proof-of-work difficulty as a multiple of the minimum difficulty.
	 * 
	 * @return
	 */
	public BigDecimal getdifficulty();

	/**
	 * Returns an object containing various state info.
	 * 
	 * @return
	 */
	public State getinfo();

	/**
	 * Returns a new coin address for receiving payments. If [account] is specified
	 * (recommended), it is added to the address book so payments received with the
	 * address will be credited to [account]. getnewaddress() always generates and
	 * returns a new address.
	 * 
	 * @return
	 */
	public String getnewaddress();

	/**
	 * Returns a new coin address for receiving payments. If [account] is specified
	 * (recommended), it is added to the address book so payments received with the
	 * address will be credited to [account].
	 * 
	 * @return
	 */
	public String getnewaddress(String account);

	/**
	 * Returns the total amount received by addresses with [account] in transactions
	 * with at least [minconf] confirmations. If [account] not provided return will
	 * include all transactions to all accounts. (version 0.3.24) [minconf=1]
	 * 
	 * @param account
	 * @return
	 */
	public BigDecimal getreceivedbyaccount(String account);

	/**
	 * Returns the total amount received by addresses with [account] in transactions
	 * with at least [minconf] confirmations. If [account] not provided return will
	 * include all transactions to all accounts. (version 0.3.24)
	 * 
	 * @param account
	 * @param minconf
	 * @return
	 */
	public BigDecimal getreceivedbyaccount(String account, long minconf);

	/**
	 * Returns the amount received by coinaddress in transactions with at least
	 * [minconf] confirmations. It correctly handles the case where someone has sent
	 * to the address in multiple transactions. Keep in mind that addresses are only
	 * ever used for receiving transactions. Works only for addresses in the local
	 * wallet, external addresses will always show 0. [minconf=1]
	 * 
	 * @param coinaddress
	 * @return
	 */
	public BigDecimal getreceivedbyaddress(String coinaddress);

	/**
	 * Returns the amount received by coinaddress in transactions with at least
	 * [minconf] confirmations. It correctly handles the case where someone has sent
	 * to the address in multiple transactions. Keep in mind that addresses are only
	 * ever used for receiving transactions. Works only for addresses in the local
	 * wallet, external addresses will always show 0.
	 * 
	 * @param coinaddress
	 * @return
	 */
	public BigDecimal getreceivedbyaddress(String coinaddress, long minconf);

	/**
	 * Get detailed information about in-wallet transaction
	 * 
	 * @param txid
	 * @return
	 */
	/**
	 * @param txid
	 * @return
	 */
	public MultiTransaction gettransaction(String txid);

	/**
	 * Returns Object that has account names as keys, account balances as values.
	 * [minconf=1]
	 */
	public Map<String, BigDecimal> listaccounts();

	/**
	 * Returns Object that has account names as keys, account balances as values.
	 */
	public Map<String, BigDecimal> listaccounts(int minconf);

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include accounts that haven't received
	 * any payments. [minconf=1] [includeempty=false]
	 * 
	 * @return
	 */
	public List<ReceivedByAccount> listreceivedbyaccount();

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include accounts that haven't received
	 * any payments. [includeempty=false]
	 * 
	 * @return
	 */
	public List<ReceivedByAccount> listreceivedbyaccount(long minconf);

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include accounts that haven't received
	 * any payments.
	 * 
	 * @return
	 */
	public List<ReceivedByAccount> listreceivedbyaccount(long minconf, boolean includeempty);

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include addresses that haven't received
	 * any payments. [minconf=1] [includeempty=false]
	 * 
	 * @return
	 */
	public List<ReceivedByAddress> listreceivedbyaddress();

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include addresses that haven't received
	 * any payments. [includeempty=false]
	 * 
	 * @return
	 */
	public List<ReceivedByAddress> listreceivedbyaddress(long minconf);

	/**
	 * [minconf] is the minimum number of confirmations before payments are
	 * included. [includeempty] whether to include addresses that haven't received
	 * any payments.
	 * 
	 * @return
	 */
	public List<ReceivedByAddress> listreceivedbyaddress(long minconf, boolean includeempty);

	/**
	 * Get all transactions in blocks since block [blockhash], or all transactions
	 * if omitted. [blockhash] [target-confirmations]
	 * 
	 * @return
	 */
	public TransactionsSinceBlock listsinceblock();

	/**
	 * Get all transactions in blocks since block [blockhash], or all transactions
	 * if omitted. [target-confirmations]
	 * 
	 * @return
	 */
	public TransactionsSinceBlock listsinceblock(String blockhash);

	/**
	 * Get all transactions in blocks since block [blockhash], or all transactions
	 * if omitted. [target-confirmations] intentionally does not affect the list of
	 * returned transactions, but only affects the returned "lastblock" value.
	 * Default value of [target-confirmations] is 1 (last block).
	 * 
	 * @return
	 */
	@JsonRpcMethod(value = "listsinceblock", paramsPassMode = JsonRpcParamsPassMode.ARRAY)
	public TransactionsSinceBlock listsinceblock(@JsonRpcParam("blockhash") String blockhash,
			@JsonRpcParam("target-confirmations") long targetConfirmations);

	/**
	 * Returns up to [count] most recent transactions skipping the first [from]
	 * transactions for account [account]. The listtransactions <account> [N] method
	 * returns the last N (default 10) transactions that affected the account's
	 * balance. "listtransactions '*' [N]" will return the last N transactions for
	 * all accounts. [account] [count=10] [from=0]
	 * 
	 * @return
	 */
	public List<Transaction> listtransactions();

	/**
	 * Returns up to [count] most recent transactions skipping the first [from]
	 * transactions for account [account]. The listtransactions <account> [N] method
	 * returns the last N (default 10) transactions that affected the account's
	 * balance. "listtransactions '*' [N]" will return the last N transactions for
	 * all accounts. [count=10] [from=0]
	 * 
	 * @return
	 */
	public List<Transaction> listtransactions(String account);

	/**
	 * Returns up to [count] most recent transactions skipping the first [from]
	 * transactions for account [account]. The listtransactions <account> [N] method
	 * returns the last N (default 10) transactions that affected the account's
	 * balance. "listtransactions '*' [N]" will return the last N transactions for
	 * all accounts. [from=0]
	 * 
	 * @return
	 */
	public List<Transaction> listtransactions(String account, long count);

	/**
	 * Returns up to [count] most recent transactions skipping the first [from]
	 * transactions for account [account]. The listtransactions <account> [N] method
	 * returns the last N (default 10) transactions that affected the account's
	 * balance. "listtransactions '*' [N]" will return the last N transactions for
	 * all accounts.
	 * 
	 * @return
	 */
	public List<Transaction> listtransactions(String account, long count, long from);

	/**
	 * Returns array of unspent transaction outputs with between minconf and maxconf
	 * (inclusive) confirmations. Optionally filtered to only include txouts paid to
	 * specified addresses. [minconf=1] [maxconf=9999999] ["address",...]
	 * 
	 * @return
	 */
	public List<UnspentTransaction> listunspent();

	/**
	 * Returns array of unspent transaction outputs with between minconf and maxconf
	 * (inclusive) confirmations. Optionally filtered to only include txouts paid to
	 * specified addresses. [maxconf=9999999] ["address",...]
	 * 
	 * @return
	 */
	public List<UnspentTransaction> listunspent(long minconf);

	/**
	 * Returns array of unspent transaction outputs with between minconf and maxconf
	 * (inclusive) confirmations. Optionally filtered to only include txouts paid to
	 * specified addresses. ["address",...]
	 * 
	 * @return
	 */
	public List<UnspentTransaction> listunspent(long minconf, long maxconf);

	/**
	 * Returns array of unspent transaction outputs with between minconf and maxconf
	 * (inclusive) confirmations. Optionally filtered to only include txouts paid to
	 * specified addresses.
	 * 
	 * @return
	 */
	public List<UnspentTransaction> listunspent(long minconf, long maxconf, String... addresses);

	/**
	 * Move from one account in your wallet to another. Use the move method to
	 * transfer balances from one account to another. Moves from the default account
	 * to any other account always succeed; moves from any other account will fail
	 * if the account has insufficient funds. Moves are not broadcast to the
	 * network, and never incur transaction fees; they just adjust account balances
	 * in the wallet. [minconf=1] [comment]
	 * 
	 * @param fromaccount
	 * @param toaccount
	 * @param amount
	 * @return
	 */
	public boolean move(String fromaccount, String toaccount, BigDecimal amount);

	/**
	 * Move from one account in your wallet to another. Use the move method to
	 * transfer balances from one account to another. Moves from the default account
	 * to any other account always succeed; moves from any other account will fail
	 * if the account has insufficient funds. Moves are not broadcast to the
	 * network, and never incur transaction fees; they just adjust account balances
	 * in the wallet. [comment]
	 * 
	 * @param fromaccount
	 * @param toaccount
	 * @param amount
	 * @return
	 */
	public boolean move(String fromaccount, String toaccount, BigDecimal amount, long minconf);

	/**
	 * Move from one account in your wallet to another. Use the move method to
	 * transfer balances from one account to another. Moves from the default account
	 * to any other account always succeed; moves from any other account will fail
	 * if the account has insufficient funds. Moves are not broadcast to the
	 * network, and never incur transaction fees; they just adjust account balances
	 * in the wallet.
	 * 
	 * @param fromaccount
	 * @param toaccount
	 * @param amount
	 * @return
	 */
	public boolean move(String fromaccount, String toaccount, BigDecimal amount, long minconf, String comment);

	/**
	 * <p>
	 * Will send the given amount to the given address, ensuring the account has a
	 * valid balance using [minconf] confirmations. Returns the transaction ID if
	 * successful. Amount is a real and is rounded to the nearest 0.00000001.
	 * </p>
	 * 
	 * <p>
	 * The sendfrom method sends coins and debits the specified account. It does
	 * **not** change coin's algorithm for selecting which coins in the wallet are
	 * sent-- you should think of the coins in the wallet as being mixed together
	 * when they are received. [minconf=1] [comment] [comment-to]
	 * </p>
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param tocoinaddress
	 * @param amount
	 * @return
	 */
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount);

	/**
	 * <p>
	 * Will send the given amount to the given address, ensuring the account has a
	 * valid balance using [minconf] confirmations. Returns the transaction ID if
	 * successful. Amount is a real and is rounded to the nearest 0.00000001.
	 * </p>
	 * 
	 * <p>
	 * The sendfrom method sends coins and debits the specified account. It does
	 * **not** change coin's algorithm for selecting which coins in the wallet are
	 * sent-- you should think of the coins in the wallet as being mixed together
	 * when they are received. [comment] [comment-to]
	 * </p>
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param tocoinaddress
	 * @param amount
	 * @return
	 */
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount, long minconf);

	/**
	 * <p>
	 * Will send the given amount to the given address, ensuring the account has a
	 * valid balance using [minconf] confirmations. Returns the transaction ID if
	 * successful. Amount is a real and is rounded to the nearest 0.00000001.
	 * </p>
	 * 
	 * <p>
	 * The sendfrom method sends coins and debits the specified account. It does
	 * **not** change coin's algorithm for selecting which coins in the wallet are
	 * sent-- you should think of the coins in the wallet as being mixed together
	 * when they are received. [comment-to]
	 * </p>
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param tocoinaddress
	 * @param amount
	 * @return
	 */
	public String sendfrom(String fromaccount, String tocoinaddress, BigDecimal amount, long minconf, String comment);

	/**
	 * <p>
	 * Will send the given amount to the given address, ensuring the account has a
	 * valid balance using [minconf] confirmations. Returns the transaction ID if
	 * successful. Amount is a real and is rounded to the nearest 0.00000001.
	 * </p>
	 * 
	 * <p>
	 * The sendfrom method sends coins and debits the specified account. It does
	 * **not** change coin's algorithm for selecting which coins in the wallet are
	 * sent-- you should think of the coins in the wallet as being mixed together
	 * when they are received.
	 * </p>
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param tocoinaddress
	 * @param amount
	 * @return
	 */
	@JsonRpcMethod(value = "sendfrom", paramsPassMode = JsonRpcParamsPassMode.ARRAY)
	public String sendfrom(@JsonRpcParam("fromaccount") String fromaccount,
			@JsonRpcParam("tocoinaddress") String tocoinaddress, @JsonRpcParam("amount") BigDecimal amount,
			@JsonRpcParam("minconf") long minconf, @JsonRpcParam("comment") String comment,
			@JsonRpcParam("comment-to") String commentto);

	/**
	 * Will send the given amounts to the given addresses. Returns the transaction
	 * ID if successful. [minconf=1] [comment]
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param addressesAmounts
	 * @return
	 */
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts);

	/**
	 * Will send the given amounts to the given addresses. Returns the transaction
	 * ID if successful.[comment]
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param addressesAmounts
	 * @return
	 */
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts, long minconf);

	/**
	 * Will send the given amounts to the given addresses. Returns the transaction
	 * ID if successful.
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param fromaccount
	 * @param addressesAmounts
	 * @return
	 */
	public String sendmany(String fromaccount, Map<String, BigDecimal> addressesAmounts, long minconf, String comment);

	/**
	 * Will send the given amount to the given address. Returns the transaction ID
	 * if successful. [comment] [comment-to]
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param coinaddress
	 * @param amount
	 * @return
	 */
	public String sendtoaddress(String coinaddress, BigDecimal amount);

	/**
	 * Will send the given amount to the given address. Returns the transaction ID
	 * if successful. [comment-to]
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param coinaddress
	 * @param amount
	 * @return
	 */
	public String sendtoaddress(String coinaddress, BigDecimal amount, String comment);

	/**
	 * Will send the given amount to the given address. Returns the transaction ID
	 * if successful.
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param coinaddress
	 * @param amount
	 * @return
	 */
	@JsonRpcMethod(value = "sendtoaddress", paramsPassMode = JsonRpcParamsPassMode.ARRAY)
	public String sendtoaddress(@JsonRpcParam("coinaddress") String coinaddress,
			@JsonRpcParam("amount") BigDecimal amount, @JsonRpcParam("comment") String comment,
			@JsonRpcParam("comment-to") String commentTo);

	/**
	 * Sets the account associated with the given address. Assigning address that is
	 * already assigned to the same account will create a new address associated
	 * with that account.
	 * 
	 * setaccount changes the account associated with an existing address. Coins
	 * previously received on that address (if any) will be debited from the
	 * previous account's balance and credited to the address' new account. Note
	 * that doing so may make the previous account's balance negative.
	 * 
	 * @param coinaddress
	 * @param account
	 * @return
	 */
	public void setaccount(String coinaddress, String account);

	/**
	 * <code>amount</code> is a real and is rounded to the nearest 0.00000001
	 * 
	 * @param amount
	 * @return
	 */
	public boolean settxfee(BigDecimal amount);

	/**
	 * Sign a message with the private key of an address.
	 * 
	 * @param coinaddress
	 * @param message
	 * @return
	 */
	public String signmessage(String coinaddress, String message);

	/**
	 * Return information about <code>coinaddress</code>.
	 * 
	 * <p>
	 * <b>Requires unlocked wallet.</b>
	 * </p>
	 * 
	 * @param coinaddress
	 * @return
	 */
	public AddressValidation validateaddress(String coinaddress);

	/**
	 * Verify a signed message.
	 * 
	 * @param coinaddress
	 * @param signature
	 * @param message
	 * @return
	 */
	public boolean verifymessage(String coinaddress, String signature, String message);

}
