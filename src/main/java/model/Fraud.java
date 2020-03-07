package model;

public class Fraud {

    private final Transaction transaction;

    public Fraud(Transaction transaction){
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

}
