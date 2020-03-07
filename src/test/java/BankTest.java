import model.Account;
import model.Bank;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BankTest {

    private Account account;
    private Bank bank;


    @Before
    public void init() {
        bank = new Bank();
    }

    @Test
    public void testBankOpenAccount() {
        account = bank.openAccount(0);
        HashMap<String, Account> accounts = bank.getAccounts();
        String mapKey = account.getNumber();

        assertThat(accounts, IsMapContaining.hasKey(mapKey));
    }

    @Test
    public void testBankOpenMultipleAccounts() {
        int accountsToOpen = 123;

        for (int i = 0; i < accountsToOpen; i++)
            bank.openAccount(0);

        assertEquals(accountsToOpen, bank.getAccounts().size());
    }

    @Test
    public void testBankCantTransferFromLockedToUnlockedAccount() {

        final long ACCOUNT_BALANCE = 100_000;

        Bank bank = new Bank();
        Account account1 = bank.openAccount(ACCOUNT_BALANCE);
        Account account2 = bank.openAccount(ACCOUNT_BALANCE);
        account1.setBlocked(true);
        account2.setBlocked(false);

        try {
            bank.transfer(account1.getNumber(), account2.getNumber(), 10_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long[] accountsBalance = new Long[] {
                bank.getBalance(account1.getNumber()),
                bank.getBalance(account1.getNumber())};

        Long[] expectedAccountsBalance = new Long[] { ACCOUNT_BALANCE, ACCOUNT_BALANCE };

        assertThat(accountsBalance, is(expectedAccountsBalance));
    }

    @Test
    public void testBankCantTransferFromUnlockedToLockedAccount() {

        final long ACCOUNT_BALANCE = 100_000;

        Bank bank = new Bank();
        Account account1 = bank.openAccount(ACCOUNT_BALANCE);
        Account account2 = bank.openAccount(ACCOUNT_BALANCE);
        account1.setBlocked(false);
        account2.setBlocked(true);

        try {
            bank.transfer(account1.getNumber(), account2.getNumber(), 10_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long[] accountsBalance = new Long[] {
                bank.getBalance(account1.getNumber()),
                bank.getBalance(account1.getNumber())};

        Long[] expectedAccountsBalance = new Long[] { ACCOUNT_BALANCE, ACCOUNT_BALANCE };

        assertThat(accountsBalance, is(expectedAccountsBalance));
    }

    @Test
    public void testBankCantTransferToLockedAccount() {
        final long ACCOUNT_BALANCE = 100_000;

        Bank bank = new Bank();
        Account account1 = bank.openAccount(ACCOUNT_BALANCE);
        Account account2 = bank.openAccount(ACCOUNT_BALANCE);
        account1.setBlocked(true);
        account2.setBlocked(true);

        try {
            bank.transfer(account1.getNumber(), account2.getNumber(), 10_000);
            bank.transfer(account2.getNumber(), account1.getNumber(), 10_000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long[] accountsBalance = new Long[] {
                bank.getBalance(account1.getNumber()),
                bank.getBalance(account1.getNumber())};

        Long[] expectedAccountsBalance = new Long[] { ACCOUNT_BALANCE, ACCOUNT_BALANCE };

        assertThat(accountsBalance, is(expectedAccountsBalance));
    }

    @Test
    public void testBankGetBalance() {
        long expectedBalance = 100_000;

        Account account = bank.openAccount(0);
        account.setBalance(expectedBalance);

        long balance = bank.getBalance(account.getNumber());
        assertEquals(expectedBalance, balance);
    }

    @Test
    public void testBankLocksAccountsWithFraudTransactions() {
        final long ACCOUNT_BALANCE = 100_000_000;

        Bank bank = new Bank();
        bank.setDebug(true);

        Account account1 = bank.openAccount(ACCOUNT_BALANCE);
        Account account2 = bank.openAccount(ACCOUNT_BALANCE);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executor.execute(() ->
            {
                try {
                    bank.transfer(account1.getNumber(), account2.getNumber(), 100_000);
                } catch (Exception e) {;}
            });
            executor.execute(() ->
            {
                try {
                    bank.transfer(account2.getNumber(), account1.getNumber(), 100_000);
                } catch (Exception e) {}
            });
        }
        executor.shutdown();

        try {
            if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("        Account1: " + account1);
        System.out.println("        Account2: " + account2);
        System.out.println();
        System.out.println("      Bank funds: " + bank.getBankFunds());
        System.out.println("Accounts balance: " + bank.getAccountsBalance());

        long numLockedAccounts = bank.getAccounts().values().stream().filter(Account::isBlocked).count();
        assertTrue(numLockedAccounts > 1);
    }

}
