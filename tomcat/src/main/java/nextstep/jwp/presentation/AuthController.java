package nextstep.jwp.presentation;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.UserRequest;

import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Override
    public ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody) {
        String path = httpHeader.getStartLine().split(" ")[1];

        if (path.startsWith("/login")) {
            return login(path);
        }
        return register(path);
    }

    private ResponseEntity login(final String path) {
        final QueryParam queryParam = new QueryParam(path);
        if (queryParam.matchParameters("account") && queryParam.matchParameters("password")) {

            UserRequest userRequest = new UserRequest(queryParam.getValue("account"),
                    queryParam.getValue("password"));

            final Optional<User> user = InMemoryUserRepository.findByAccount(userRequest.getAccount());
            if (user.isPresent()) {
                LOGGER.info(user.get().toString());
                return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/index.html");
            }
            return new ResponseEntity(StatusCode.UNAUTHORIZED, "/401.html");
        }
        throw new QueryParamNotFoundException();
    }

    private ResponseEntity register(final String path) {
        return new ResponseEntity(StatusCode.OK, path);
    }
}
