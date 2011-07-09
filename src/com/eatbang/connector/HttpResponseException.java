package com.eatbang.connector;

import java.io.IOException;

public class HttpResponseException extends IOException {
	public HttpResponseException() {
		super();
		statusCode = 0;
	}

	public HttpResponseException(int statusCode) {
		super("Server rejected HTTP request; status code = " + statusCode);
		this.statusCode = statusCode;
	}

	public HttpResponseException(String description, int statusCode) {
		super(description);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	private static final long serialVersionUID = 4197096101186991787L;

	private final int statusCode;
}
