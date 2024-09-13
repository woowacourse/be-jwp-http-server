package org.apache;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.HttpRequestFixture;

class RegisterControllerTest {

	RegisterController controller = new RegisterController();

	@DisplayName("GET 요청의 경우 register.html을 반환한다.")
	@Test
	void handle_GET() throws Exception {
		// given
		HttpRequest request = HttpRequestFixture.createGetMethod("/register");
		HttpResponse response = HttpResponse.empty();

		// when
		controller.doGet(request, response);

		// then
		URL resource = Http11Processor.class.getClassLoader().getResource("static/register.html");
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		var responseBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		assertThat(response.getResponseBody()).isEqualTo(responseBody);
	}

	@DisplayName("POST 요청의 경우 회원가입을 진행한다.")
	@Test
	void handle_POST() throws Exception {
		// given
		HttpRequest request = HttpRequestFixture.createRegisterPostMethod();
		HttpResponse response = HttpResponse.empty();

		// when
		controller.doPost(request, response);

		// then
		assertThat(response.getHeaders()).contains("Location: /index.html");
	}

	@DisplayName("이미 존재하는 account로 회원가입을 시도할 경우 401.html을 리다이렉트한다.")
	@Test
	void handle_POST_alreadyExist() throws Exception {
		// given
		HttpRequest request = HttpRequestFixture.createRegisterPostMethodWithAlreadyExistAccount();
		HttpResponse response = HttpResponse.empty();

		// when
		controller.doPost(request, response);

		// then
		assertThat(response.getHeaders()).contains("Location: /401.html");
	}
}
