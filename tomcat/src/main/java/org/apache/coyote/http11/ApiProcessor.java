package org.apache.coyote.http11;

import static org.apache.coyote.http11.MethodType.GET;
import static org.apache.coyote.http11.MethodType.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class ApiProcessor {

    private final PageProcessor pageProcessor;

    public ApiProcessor() {
        this.pageProcessor = new PageProcessor();
    }

    public void process(
            Socket connection,
            String requestPath,
            MethodType methodType,
            Map<String, String> requestHeader,
            Map<String, String> requestBody
    ) throws IOException {
        OutputStream outputStream = connection.getOutputStream();

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            if (methodType.isPost()) {
                processLogin(outputStream, requestBody);
                return;
            }
            if (methodType.isGet()) {
                SessionManager sessionManager = SessionManager.getInstance();
                if (requestHeader.containsKey("Cookie")) {
                    String jsessionid = requestHeader.get("Cookie").split("=")[1];
                    Session session = sessionManager.findSession(jsessionid);
                    if (session != null && session.getAttribute("user") != null) {
                        User user = (User) session.getAttribute("user");
                        loginSuccess(outputStream, user);
                        return;
                    }
                }
                processLoginPage(outputStream);
                return;
            }
        }

        if (requestUri.equals("/register")) {
            if (methodType.isPost()) {
                processRegisterPost(outputStream, requestBody);
                return;
            }
            if (methodType.isGet()) {
                pageProcessor.process(outputStream, "register");
            }
        }
    }

    private void processRegisterPost(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        pageProcessor.processWithHttpStatus(outputStream, "index", HttpStatus.CREATED);
    }

    private Map<String, String> findParameterEntries(String requestPath) {
        String[] splitPath = requestPath.split("\\?");
        String[] queryParameters = splitPath[1].split("&");
        Map<String, String> parameterMap = new HashMap<>();
        for (String queryParameter : queryParameters) {
            String[] queryParameterEntry = queryParameter.split("=");
            parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
        }
        return parameterMap;
    }

    private void processLogin(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 User입니다."));

        if (user.checkPassword(password)) {
            loginSuccess(outputStream, user);
            return;
        }
        loginFail(outputStream);
    }

    private void processLoginPage(OutputStream outputStream) throws IOException {
        pageProcessor.process(outputStream, "login");

    }

    private void loginSuccess(OutputStream outputStream, User user) throws IOException {
        Session session = new Session(user.getId().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        final var response = String.join("\r\n",
                "HTTP/1.1 " + HttpStatus.FOUND.getHeaderForm(),
                "Location: " + " http://localhost:8080/",
                "Set-Cookie: JSESSIONID=" + user.getId().toString(),
                "");

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void loginFail(OutputStream outputStream) throws IOException {
        pageProcessor.processWithHttpStatus(outputStream, "401", HttpStatus.UNAUTHORIZED);
    }
}
