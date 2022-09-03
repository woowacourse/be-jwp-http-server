package org.apache.coyote.http11.request;

public enum Method {

	GET,
	POST,
	PUT,
	PATCH,
	;

	public static Method findBy(final String method) {
		try {
			return valueOf(method.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("존재하지 않는 HTTP Method 입니다. [%s]", method));
		}
	}
}
