package Exceptions;

public class AccountIsBlockedException extends BankException {
    public AccountIsBlockedException(String errorMessage) {
        super(errorMessage);
    }
}
