package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterController implements Controller {
    @Override
    public void doProcess(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
        }
        if ("POST".equals(method)) {
            doPost(request, response);
        }
    }

    private void doGet(HttpRequest request, HttpResponse response) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + request.getPath() + ".html");
        Path filePath = new File(url.getFile()).toPath();
        String responseBody = new String(Files.readAllBytes(filePath));
        response.setStatus(200);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
        response.write(responseBody);
        response.flush();
    }

    private void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        InMemoryUserRepository.save(new User(2, account, password, email));
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.setStatus(302);
            response.sendRedirect("/500.html");
        } else {
            response.setStatus(302);
            response.sendRedirect("/index.html");
        }
    }
}
