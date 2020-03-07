import model.Bank;
import org.junit.Before;
import org.junit.Test;
import utils.Runner;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

    private Bank bank;
    private Runner runner;
    private long initialBankFunds;

    @Before
    public void init() {
        final int NUM_ACCOUNTS = 100;
        final int NUM_TRANSACTIONS = 100;

        runner = new Runner(NUM_ACCOUNTS, NUM_TRANSACTIONS);
        runner.setDebug(true);

        bank = runner.getBank();
        initialBankFunds = bank.getBankFunds();
    }

    @Test
    public void testTransfers() {
        initialBankFunds = bank.getBankFunds();

        runner.run();

        long remainingFunds = bank.getBankFunds();
        long accountsBalance = bank.getAccountsBalance();

        System.out.println();
        System.out.println(" Initial bank funds: " + initialBankFunds);
        System.out.println(" Current bank funds: " + remainingFunds);
        System.out.println(" Accounts balance: " + accountsBalance);

        System.out.println();
        System.out.println(" Total transactions: " + bank.getTransactions().size());
        System.out.println(" Failed transactions: " + bank.getFailedTransactions().size());
        System.out.println(" Fraud transactions: " + bank.getFraudTransactions().size());

        assertEquals(initialBankFunds, remainingFunds + accountsBalance);
    }
}
