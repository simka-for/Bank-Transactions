package Exceptions;

public class BankTransferException extends RuntimeException {
    public BankTransferException(String errorMessage) {
        super(errorMessage);
    }
}
