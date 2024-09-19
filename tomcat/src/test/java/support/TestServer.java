package support;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import org.apache.catalina.connector.CatalinaConnectionListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.adapter.CoyoteAdapter;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.session.SessionManager;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestServer {
    private static final String HOST = "http://localhost:11240";
    private static final AtomicBoolean isOpen = new AtomicBoolean(false);

    private static final RequestMapper CONTROLLER_EXECUTOR = new RequestMapper(
            Map.of("/login", new LoginController(),
                    "/register", new RegisterController())
    );

    private static final SessionManager sessionManager = new SessionManager();

    public static void serverStart(final Tomcat tomcat) {
        start(tomcat);
    }

    public static void serverStart() {
        if (isOpen.get() == true) {
            return;
        }
        final Tomcat tomcat = new Tomcat(
                new Connector(11240, 50,
                        new CatalinaConnectionListener(new CoyoteAdapter(CONTROLLER_EXECUTOR, new ResourceController(), sessionManager),
                                Executors.newCachedThreadPool())
                )
        );
        start(tomcat);
    }

    public static String getHost() {
        return HOST;
    }

    private static void start(final Tomcat tomcat) {
        isOpen.set(true);
        final Runnable serverTask = () -> tomcat.start();
        final Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
