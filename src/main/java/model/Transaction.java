package model;

import utils.Generator;

public class Transaction {

    private final String id;
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final long amount;
    private final TransactionStatus transactionStatus;

    public Transaction(String fromAccountNum, String toAccountNum, long amount, TransactionStatus transactionStatus) {
        this.id = Generator.requestSimpleRandomID();
        this.fromAccountNumber = fromAccountNum;
        this.toAccountNumber = toAccountNum;
        this.amount = amount;
        this.transactionStatus = transactionStatus;
    }

    public String getId() {
        return id;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public long getAmount() {
        return amount;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction other = (Transaction) o;

        return (this.fromAccountNumber.equals(other.fromAccountNumber) &&
                this.toAccountNumber.equals(other.toAccountNumber));
    }
}
