package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long autoIncrement = 0L;

    static {
        final User user = new User(++autoIncrement, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static User save(User input) {
        if (database.containsKey(input.getAccount())) {
            throw new IllegalArgumentException("이미 존재하는 User 계정입니다.");
        }

        User user = new User(++autoIncrement, input.getAccount(), input.getPassword(), input.getEmail());
        database.put(input.getAccount(), input);

        return user;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static void clear() {
        database.clear();
    }
}
