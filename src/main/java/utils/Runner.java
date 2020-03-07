package utils;

import model.Bank;
import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class Runner {

    private static List<Thread> threads = new ArrayList<>();
    private List<List<Transaction>> transactionQueue;

    private static int elapsedTime = 0;
    private static boolean debug = false;

    private int numJobs;
    private int numAccounts;
    private int numTransactions;

    private Bank bank;

    public Runner(int numAccounts, int numTransactions) {
        this(Runtime.getRuntime().availableProcessors(), numAccounts, numTransactions);
    }

    public Runner(int numJobs, int numAccounts, int numTransactions) {
        this.numJobs = numJobs;
        this.numAccounts = numAccounts;
        this.numTransactions = numTransactions;

        bank = new Bank();
        transactionQueue = new ArrayList<>();
    }

    public void run() {
        final long TIME_START = System.currentTimeMillis();

        // Setup Bank
        setupBank();

        // Setup Pending Transactions
        setupTransactionQueue();

        // Setup threads
        setupThreads();

        // Start threads
        startThreads();

        // Join threads
        joinThreads();

        elapsedTime = (int) (System.currentTimeMillis() - TIME_START);

        Generator.consoleLog("\n[%s] Finished in %d ms!\n", debug, Thread.currentThread().getName(), elapsedTime);
    }

    private static void joinThreads() {
        for (Thread t : threads) {
            try {
                t.join();
                Generator.consoleLog("[%s] Finished!\n", debug, t.getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startThreads() {
        for (Thread t : threads) {
            Generator.consoleLog("[%s] started!\n", debug, t.getName());
            t.start();
        }
    }

    private void setupThreads() {
        for (List<Transaction> transactions : transactionQueue) {
            Thread thread = new Thread(() -> {
                final long TIME_START = System.currentTimeMillis();
                for (Transaction transaction : transactions) {
                    final String FROM = transaction.getFromAccountNumber();
                    final String TO = transaction.getToAccountNumber();
                    final long AMOUNT = transaction.getAmount();

                    try {
                        bank.transfer(FROM, TO, AMOUNT);
                        System.out.println("");
                    } catch (Exception e) {
                        Generator.consoleLog("\n[%s, BANK TRANSFER ERROR]\n",
                                debug, Thread.currentThread().getName(), e.getMessage());
                    }
                }
                int threadRunningTime = (int) (System.currentTimeMillis() - TIME_START);
                Generator.consoleLog("\n[%s] Finished in %d ms!\n",
                        debug, Thread.currentThread().getName(), threadRunningTime);
            });

            threads.add(thread);
        }

        int counter = 0;
        for (Thread thread : threads) {
            thread.setName("T" + counter);
            counter++;
        }
    };

    private void setupBank() {
        final long INITIAL_BANK_FUNDS = bank.getBankFunds();

        for (int i = 0; i < numAccounts; i++) {
            bank.openAccount((INITIAL_BANK_FUNDS / 2) / numAccounts);
        }
    }
    private void setupTransactionQueue() {
        for (int i = 0; i < numJobs; i++) {
            List<Transaction> transactions = Generator.generateTransactions(bank.getAccounts(), numTransactions);
            transactionQueue.add(transactions);
        }
    }

}
