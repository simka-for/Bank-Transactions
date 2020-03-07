import Exceptions.AccountBalanceChangeException;
import Exceptions.AccountIsBlockedException;
import Exceptions.BankException;
import model.Account;

import org.junit.Before;
import org.junit.Test;
import utils.Generator;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    private Account accountDeposit;
    private Account accountWithdrawal;
    private long initialBalance = 100_000;

    @Before
    public void init() {
        accountDeposit = new Account(Generator.requestSimpleRandomID(), initialBalance);
        accountWithdrawal = new Account(Generator.requestSimpleRandomID(), initialBalance);;
    }

    @Test
    public void testDeposit() {
        long depositAmount = 10_000;

        try {
            accountDeposit.deposit(depositAmount);
        } catch (BankException e) {
            e.printStackTrace();
        }

        long resultingBalance = accountDeposit.getBalance();
        assertEquals(initialBalance + depositAmount, resultingBalance);
    }

    @Test
    public void testZeroDeposit() {
        long depositAmount = 0;

        Throwable throwable = assertThrows(AccountBalanceChangeException.class,
                () -> accountDeposit.deposit(depositAmount));
    }

    @Test
    public void testNegativeDeposit() {
        long depositAmount = -10_000;

        Throwable throwable = assertThrows(AccountBalanceChangeException.class,
                () -> accountDeposit.deposit(depositAmount));
    }

    @Test
    public void testDepositIfAccountIsBlocked() {
        long depositAmount = 10_000;
        accountDeposit.setBlocked(true);

        Throwable throwable = assertThrows(AccountIsBlockedException.class,
                () -> accountDeposit.deposit(depositAmount));
    }

    // Withdrawal Tests

    @Test
    public void testWithdrawal() {
        long withdrawAmount = 10_000;

        try {
            accountWithdrawal.withdraw(withdrawAmount);
        } catch (BankException e) {
            e.printStackTrace();
        }

        long resultingBalance = accountWithdrawal.getBalance();
        assertEquals(initialBalance - withdrawAmount, resultingBalance);
    }

    @Test
    public void testZeroWithdrawal() {
        long withdrawAmount = 0;

        Throwable throwable = assertThrows(AccountBalanceChangeException.class,
                () -> accountWithdrawal.withdraw(withdrawAmount));
    }

    @Test
    public void testNegativeWithdrawal() {
        long withdrawAmount = -10_000;

        Throwable throwable = assertThrows(AccountBalanceChangeException.class,
                () -> accountWithdrawal.withdraw(withdrawAmount));
    }

    @Test
    public void testMoreThanLimitWithdrawal() {
        long withdrawAmount = 200_000;

        Throwable throwable = assertThrows(AccountBalanceChangeException.class,
                () -> accountWithdrawal.withdraw(withdrawAmount));
    }

    @Test
    public void testWithdrawalIfAccountIsBlocked() {
        long withdrawAmount = 10_000;
        accountWithdrawal.setBlocked(true);

        Throwable throwable = assertThrows(AccountIsBlockedException.class,
                () -> accountWithdrawal.withdraw(withdrawAmount));
    }



}
