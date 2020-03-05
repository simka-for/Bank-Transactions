import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private final Random random = new Random();
    private ConcurrentHashMap<String, Account> accounts;

    public Bank() {
        accounts = new ConcurrentHashMap<>();
    }

    private synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return chanceBlock();
    }

    public void addAccount(String accNumber, long money) {
        accounts.put(accNumber, new Account(money, accNumber));
    }


    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account fromAccount = getAccount(fromAccountNum);
        Account toAccount = getAccount(toAccountNum);

        if (fromAccount == null) {
            System.err.println(String.format("Account #%s was not found", fromAccountNum));
            return;
        }
        if (toAccount == null) {
            System.err.println(String.format("Account #%s was not found", toAccountNum));
            return;
        }

        boolean isBusy = fromAccount.isBusy() || toAccount.isBusy();
        if (isBusy)
            System.out.println("At least one account is busy. Should wait...");
        while (isBusy) {
            isBusy = fromAccount.isBusy() || toAccount.isBusy();
        }

        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        takeLocks(lock1, lock2);
        try{
            if (fromAccount.isLocked() || toAccount.isLocked()) {
                System.out.println(fromAccount.isLocked() ? toAccount.isLocked() ?
                        String.format("Accounts #%s and #%s are locked", fromAccountNum, toAccountNum) :
                        String.format("Account #%s is locked", fromAccountNum) :
                        String.format("Account #%s is locked", toAccountNum));
            }
            if (fromAccount.isNormal() && toAccount.isNormal()) {
                fromAccount.setBusy();
                toAccount.setBusy();

                if (amount > 50000 && isFraud(fromAccountNum, toAccountNum, amount)) {
                    fromAccount.setLocked();
                    toAccount.setLocked();
                    System.out.println(String.format("Transfer %1$s from account #%2$s to #%3$s not finished and " +
                                    "in result of " +
                                    "security check accounts #%2$s and #%3$s are locked", amount, fromAccountNum,
                            toAccountNum));
                } else {
                    fromAccount.setNormal();
                    toAccount.setNormal();
                    System.out.println(String.format("Transfer %s from %s to %s is finished successfully", amount
                            , fromAccountNum, toAccountNum));
                }
                if (getBalance(fromAccountNum) >= amount && fromAccount.isNormal() && toAccount.isNormal()) {
                    fromAccount.raise(amount);
                    toAccount.put(amount);
                }
            }
        }finally {
            lock1.unlock();
            lock2.unlock();
        }
        }

    private void takeLocks(Lock lock1, Lock lock2) throws InterruptedException {
        boolean firstLock = false;
        boolean secondLock = false;

        while(true){
            try{
                firstLock = lock1.tryLock();
                secondLock = lock2.tryLock();
            }finally {
                if (firstLock && secondLock){
                    return;
                }
                if (firstLock){
                    lock1.unlock();
                }
                if (secondLock){
                    lock2.unlock();
                }
                Thread.sleep(1);
            }
        }
    }

    public Account getAccount(String number) {
        return accounts.get(number);
    }

    public long getBalance(String accountNum) {
        return getAccount(accountNum).getMoney();
    }

    public ConcurrentHashMap<String, Account> getAccounts() {
        return accounts;
    }

    private boolean chanceBlock(){     // chance of locking
        double grade = Math.random();

        return !(grade < 0.75);
    }
}