package nextstep.jwp.web.application;

import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.domain.User;

public class UserService {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("해당 계정의 유저가 없습니다."));
        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalStateException("비밀번호가 틀렸습니다.");
    }

    public User register(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account)
            .ifPresent(user -> {
                throw new IllegalStateException("해당 계정의 유저가 이미 존재합니다.");
            });
        User user = new User(2, account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }
}
