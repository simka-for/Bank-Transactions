package Exceptions;

public class AccountBalanceChangeException extends BankException {
    public AccountBalanceChangeException(String errorMessage) {
        super(errorMessage);
    }
}
