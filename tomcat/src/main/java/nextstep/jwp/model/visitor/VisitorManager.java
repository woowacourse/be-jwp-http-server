package nextstep.jwp.model.visitor;

import org.apache.catalina.session.Session;

public class VisitorManager {

    public Visitor identify(String sessionId) {
        if (sessionId == null) {
            return new NewVisitor();
        }
        return new OldVisitor(sessionId);
    }
}
