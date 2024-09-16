package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

	@DisplayName("주어진 List<String> 을 파싱하여 HttpHeaders를 반환한다.")
	@Test
	void from() {
		// given
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: text/html;charset=UTF-8");
		headers.add("cookie: JSESSIONID=hhhh");
		headers.add("Cache-Control: no-cache, no-store, must-revalidate");

		// when
		HttpHeaders httpHeaders = HttpHeaders.from(headers);

		//then
		assertThat(httpHeaders.getHeaderValues("cache-control")).contains("no-cache", "no-store",
			"must-revalidate");
		assertThat(httpHeaders.getHeader("content-type").isPresent()).isTrue();
		assertThat(httpHeaders.getHeader("content-type").get()).isEqualTo("text/html;charset=UTF-8");
		assertThat(httpHeaders.getCookie("JSESSIONID").isPresent()).isTrue();
		assertThat(httpHeaders.getCookie("JSESSIONID").get()).isEqualTo("hhhh");
	}

}
