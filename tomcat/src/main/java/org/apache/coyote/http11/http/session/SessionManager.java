package org.apache.coyote.http11.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.techcourse.model.User;

public class SessionManager {

	private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

	public static void createSession(String id, User user) {
		sessions.put(id, new Session(id, user));
	}

	public static Session getSession(String sessionId) {
		return sessions.get(sessionId);
	}
}
