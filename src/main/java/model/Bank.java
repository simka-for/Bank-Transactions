package model;

import Exceptions.BankException;
import Exceptions.BankTransferException;
import utils.Generator;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

public class Bank {

    private long bankFunds = 1_000_000_000;
    private static final int POSSIBLE_FRAUD_AMOUNT = 50_000;

    private HashMap<String, Account> accounts = new HashMap<>();

    private Hashtable<String, Transaction> transactions = new Hashtable<>();
    private Hashtable<String, Transaction> failedTransactions = new Hashtable<>();
    private Hashtable<String, Fraud> fraudTransactions = new Hashtable<>();

    private final Random random = new Random();
    private boolean debug = false;

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void processFraud(Transaction transaction) throws InterruptedException{
        if (isFraud(transaction.getFromAccountNumber(), transaction.getToAccountNumber(), transaction.getAmount())) {
            Fraud fraudTransaction = new Fraud(transaction);

            Account from = accounts.get(transaction.getFromAccountNumber());
            Account to = accounts.get(transaction.getToAccountNumber());

            from.setBlocked(true);
            to.setBlocked(true);

            fraudTransactions.put(transaction.getId(), fraudTransaction);
            Generator.consoleLog("  [%s, FRAUD: DETECTED] Transaction '%s' considered as fraud!\n",
                    debug, Thread.currentThread().getName(), transaction);

        } else {
            Generator.consoleLog("  [%s, FRAUD: NOT DETECTED] Transaction '%s' successfully cleared!\n",
                    debug, Thread.currentThread().getName(), transaction);
        }
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws Exception {

        // Error checking

        if(fromAccountNum == null || toAccountNum == null)
            throw new BankTransferException("Either fromAccountNum or toAccountNum aren't initialized!");

        if(fromAccountNum.equals(toAccountNum))
            throw new BankTransferException("Can't transfer to the same account!");

        if(amount <= 0)
            throw new BankTransferException("Negative or Zero transaction amounts aren't allowed!");

        Account from = accounts.get(fromAccountNum);
        Account to = accounts.get(toAccountNum);

        if (from == null)
            throw new BankTransferException("Account " + fromAccountNum + " not found!");

        if (to == null)
            throw new BankTransferException("Account " + toAccountNum + " not found!");

        // Transactions process

        Transaction transaction = processTransaction(from, to, amount);
        transactions.put(transaction.getId(), transaction);

        TransactionStatus status = transaction.getTransactionStatus();
        long transactionAmount = transaction.getAmount();

        if (status == TransactionStatus.FAILED) {
            failedTransactions.put(transaction.getId(), transaction);
            Generator.consoleLog("  [%s, FAILED] Transaction '%s' failed!", debug, Thread.currentThread().getName(), transaction);
            return;
        }

        if (status == TransactionStatus.SUCCESS) {
            if (transactionAmount > POSSIBLE_FRAUD_AMOUNT) {
                Generator.consoleLog("  [%s, POTENTIAL FRAUD] Transaction '%s' marked as potentially-fraud!\n",
                        debug, Thread.currentThread().getName(), transaction);
                processFraud(transaction);
                return;
            }
            Generator.consoleLog("  [%s, SUCCESS] Transaction '%s' successfully cleared!\n",
                    debug, Thread.currentThread().getName(), transaction);
        }
    }

    public Transaction processTransaction(Account fromAccount, Account toAccount, long amount){

        // Withdraw funds from first account

        try {
            fromAccount.withdraw(amount);
        } catch (BankException e) {
            Generator.consoleLog("  [%s, ACCOUNT WITHDRAWAL ERROR] %s\n", debug, Thread.currentThread().getName(), e);
            return new Transaction(fromAccount.getNumber(), toAccount.getNumber(), amount, TransactionStatus.FAILED);
        }

        // Deposit funds from 2 account

        try {
            toAccount.deposit(amount);

            return new Transaction(fromAccount.getNumber(), toAccount.getNumber(), amount, TransactionStatus.SUCCESS);
        } catch (BankException e) {

            Generator.consoleLog("  [%s, ACCOUNT DEPOSIT ERROR] %s\n", debug, Thread.currentThread().getName(), e);

            // if refunds in case of error

            try {
                fromAccount.deposit(amount);
            } catch (BankException exception) {

                Generator.consoleLog("  [%s, ACCOUNT BACK-DEPOSIT ERROR] %s\n", debug, Thread.currentThread().getName(), e);
                return new Transaction(fromAccount.getNumber(), toAccount.getNumber(), amount, TransactionStatus.FAILED);
            }
        }
        Generator.consoleLog("  [%s, ACCOUNT WITHDRAWN & DEPOSITED BACK ERROR]\n", debug, Thread.currentThread().getName());
        return new Transaction(fromAccount.getNumber(), toAccount.getNumber(), amount, TransactionStatus.FAILED);
    }



    public Account openAccount(long amount) {
        amount = (amount <= 0) ? 0 : amount ;
        long newBankFunds = getBankFunds() - amount;
        amount = (newBankFunds <= 0) ? getBankFunds() : amount;

        String accountNum;

        do {
            accountNum = Generator.requestSimpleRandomID();
        } while (accounts.containsKey(accountNum));

        Account account = new Account(accountNum);

        account.setBalance(amount);
        setBankFunds(getBankFunds() - amount);

        accounts.put(accountNum, account);

        return account;
    }


    public long getBalance(String accountNum) {
        if (accountNum == null || accountNum.isEmpty() || !accounts.containsKey(accountNum)) {
            throw new IllegalArgumentException("Account " + accountNum + " is not found!");
        }
        return accounts.get(accountNum).getBalance();
    }

    public long getAccountsBalance() {
        return accounts.values().stream()
                .map(Account::getBalance)
                .mapToLong(balance -> balance).sum();
    }

    public long getBankFunds() {
        return bankFunds;
    }

    public void setBankFunds(long bankFunds) {
        this.bankFunds = bankFunds;
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public Hashtable<String, Transaction> getTransactions() {
        return transactions;
    }

    public Hashtable<String, Transaction> getFailedTransactions() {
        return failedTransactions;
    }

    public Hashtable<String, Fraud> getFraudTransactions() {
        return fraudTransactions;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
