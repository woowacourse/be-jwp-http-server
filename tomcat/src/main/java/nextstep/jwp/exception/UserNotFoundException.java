package nextstep.jwp.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "존재하지 않는 계정입니다. -> value: %s";

    public UserNotFoundException(final String value) {
        super(String.format(ERROR_MESSAGE, value));
    }
}
