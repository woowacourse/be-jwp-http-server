package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginServiceTest")
class LoginServiceTest {

    private static final LoginService LOGIN_SERVICE = new LoginService();

    @Test
    @DisplayName("로그인 성공여부를 판별한다.")
    void loginValidate() {
        User user = new User(0L, "gugu", "password", "");

        assertThatCode(() -> LOGIN_SERVICE.loginValidate(user)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("아이디가 존재하지 않다면 에러가 발생한다.")
    void loginValidateIdException() {
        User user = new User(0L, "error", "password", "");

        assertThatThrownBy(() -> LOGIN_SERVICE.loginValidate(user))
            .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("아이디가 존재하지 않다면 에러가 발생한다.")
    void loginValidatePasswordException() {
        User user = new User(0L, "gugu", "error", "");

        assertThatThrownBy(() -> LOGIN_SERVICE.loginValidate(user))
            .isInstanceOf(UnauthorizedException.class);
    }
}