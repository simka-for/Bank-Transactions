import org.junit.Before;
import org.junit.Test;

public class BankTest {

    Bank bank;

    @Before
    public void init() {
        bank = new Bank();
        for (int i = 0; i < 10; i++) {
            bank.addAccount(String.valueOf(i), 100_000);
        }
    }

    @Test
    public void testDeadLock() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 100000; j++) {
                        bank.transfer("0", "1", 300);
                        bank.transfer("1", "0", 500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Balance of first account : " + bank.getBalance("0") +
                " And second account : " + bank.getBalance("1"));
    }

    @Test
    public void testIsFraud() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                try {
                    bank.transfer("0", "1", 51000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads)
            thread.join();
        System.out.println("Balance of first account : " + bank.getBalance("0") +
                " And second account : " + bank.getBalance("1"));
    }
}

