package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RegisterService 테스트")
class RegisterServiceTest {

    private static final String ACCOUNT = "gugu";
    private static final String EMAIL = "hkkang@woowahan.com";
    private static final String PASSWORD = "password";

    private final RegisterService registerService = new RegisterService();

    @DisplayName("회원가입 테스트 - 성공")
    @Test
    void register() {
        //given
        final String newAccount = "a" + ACCOUNT;
        final String newEmail = "a" + EMAIL;
        final User newUser = new User(newAccount, PASSWORD, newEmail);

        //when
        registerService.register(newUser);

        //then
        final User foundUser = InMemoryUserRepository.findByAccount(newAccount)
                .orElseThrow(() -> new NotFoundException("해당 account의 User가 존재하지 않습니다."));

        assertThat(foundUser.getAccount()).isEqualTo(newAccount);
        assertThatCode(() -> foundUser.validatePassword(PASSWORD))
                .doesNotThrowAnyException();
        assertThat(foundUser.getEmail()).isEqualTo(newEmail);
    }

    @DisplayName("회원가입 테스트 - 실패 - 이미 존재하는 account")
    @Test
    void registerFailureWhenAccountAlreadyExists() {
        //given
        final String newEmail = "a" + EMAIL;
        final User newUser = new User(ACCOUNT, PASSWORD, newEmail);

        //when
        //then
        assertThatThrownBy(() -> registerService.register(newUser))
                .isInstanceOf(DuplicateException.class);
    }

    @DisplayName("회원가입 테스트 - 실패 - 이미 존재하는 email")
    @Test
    void registerFailureWhenEmailAlreadyExists() {
        //given
        final String newAccount = "a" + ACCOUNT;
        final User newUser = new User(newAccount, PASSWORD, EMAIL);

        //when
        //then
        assertThatThrownBy(() -> registerService.register(newUser))
                .isInstanceOf(DuplicateException.class);
    }
}