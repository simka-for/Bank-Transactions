package model;

import Exceptions.AccountBalanceChangeException;
import Exceptions.AccountIsBlockedException;

public class Account implements Comparable<Account> {

    private final String number;
    private long balance;
    private final long BALANCE_LIMIT = 0;
    private boolean isBlocked = false;

    public Account(String number) {
        this.number = number;
    }

    public Account(String number, long balance) {
        this.number = number;
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }


    public synchronized long deposit(long amount) throws AccountIsBlockedException, AccountBalanceChangeException {
        checkIsBlocked();

        if (amount <= 0)
            throw new AccountBalanceChangeException("Negative or Zero deposit isn't allowed.");
        return balance += amount;
    }

    public synchronized long withdraw(long amount) throws AccountIsBlockedException, AccountBalanceChangeException {
        checkIsBlocked();

        if (amount <= 0)
            throw new AccountBalanceChangeException("Negative or Zero withdrawal isn't allowed.");

        long afterWithdrawalBalance = balance - amount;

        if (afterWithdrawalBalance < BALANCE_LIMIT)
            throw new AccountBalanceChangeException("Account " + number + " went out of funds!");

        balance = afterWithdrawalBalance;

        return balance;
    }

    private void checkIsBlocked() throws AccountIsBlockedException {
        if (isBlocked())
            throw new AccountIsBlockedException("Account " + number + " is blocked!");
    }

    @Override
    public int compareTo(Account o) {
        return this.getNumber().compareTo(o.getNumber());
    }

    @Override
    public String toString() {
        return "Account{" +
                "number='" + number + '\'' +
                ", balance=" + balance +
                ", BALANCE_LIMIT=" + BALANCE_LIMIT +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
