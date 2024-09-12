package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

	@DisplayName("/ 요청에는 RootPageHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestRootPage() {
		String requestPath = "/";

		Class<RootPageHandler> expectedHandler = RootPageHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("/login 요청에는 LoginHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestLogin() {
		String requestPath = "/login";

		Class<LoginHandler> expectedHandler = LoginHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("정적 리소스 요청에는 ResourceHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestResource() {
		String requestPath = "/css/test.css";

		Class<ResourceHandler> expectedHandler = ResourceHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("찾는 핸들러가 없는 경우 404 핸들러를 반환한다.")
	@Test
	void findHandler_WhenHandlerNotFound() {
		String requestPath = "/notfound";

		Class<NotFoundHandler> expectedHandler = NotFoundHandler.class;

		runTest(requestPath, expectedHandler);
	}

	private void runTest(String requestPath, Class<? extends Handler> expectedHandler) {
		HttpRequest request = new HttpRequest("GET " + requestPath + " HTTP/1.1", List.of(), null);

		Handler handler = HandlerMapper.findHandler(request);

		assertThat(handler).isExactlyInstanceOf(expectedHandler);
	}
}
