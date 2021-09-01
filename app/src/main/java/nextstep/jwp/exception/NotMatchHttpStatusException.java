package nextstep.jwp.exception;

public class NotMatchHttpStatusException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 상태 코드입니다.";

    public NotMatchHttpStatusException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
