package utils;

import model.Account;
import model.Transaction;
import model.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Generator {

    // GeneratorID

    public static String requestSimpleRandomID() {
        final String CHARS = "0123456789";
        final int LENGTH = 20;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            int index = getRandomIntInRange(0, CHARS.length() - 1);
            char ch = CHARS.charAt(index);

            stringBuilder.append(ch);
        }

        return stringBuilder.toString();
    }

    private static int getRandomIntInRange(int min, int max) {
        if (min >= max)
            throw new IllegalArgumentException("Err! Max has to be > Min!");

        return new Random().nextInt((max - min) + 1) + min;
    }

    // Logger

    public static void consoleLog(String message, boolean debug, Object... args) {
        if (debug) System.out.println(String.format(message, args));
    }

    // ==================

    public static List<Transaction> generateTransactions(HashMap<String, Account> accounts, int numTransactions) {
        if (accounts == null || accounts.isEmpty() || accounts.size() < 2)
            throw new IllegalArgumentException("Accounts can't be null, empty or have less than 2 items.");

        List<Transaction> transactions = new ArrayList<>();
        List<String> accountNumbers = new ArrayList<String>(accounts.keySet());

        for (int i = 0; i < numTransactions; i++) {
            long amount = (Math.random() < 0.05) ? 100_000 : 50_000;
            int randomFromAccountNumberIndex = getRandomIntInRange(0, accountNumbers.size() - 1);
            String randomFromAccountNumber = accountNumbers.get(randomFromAccountNumberIndex);

            int randomToAccountNumberIndex;
            String randomToAccountNumber;

            do {
                randomToAccountNumberIndex = getRandomIntInRange(0, accountNumbers.size() - 1);
                randomToAccountNumber = accountNumbers.get(randomToAccountNumberIndex);
            } while (randomToAccountNumberIndex == randomFromAccountNumberIndex);

            transactions.add(new Transaction(randomFromAccountNumber, randomToAccountNumber,
                    amount, TransactionStatus.PENDING));
        }

        return transactions;
    }

    // GeneratorID test

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            String uuid = requestSimpleRandomID();
            System.out.printf("%2d) UUID: %s\n", i + 1, uuid);
        }
    }

}
