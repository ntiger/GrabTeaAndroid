package com.eatbang.connector;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.eatbang.util.PropertyManager;

public class EatBangConnection {

	private static final int DEFAULT_PORT = 80;
	private static final String LOG_TAG = "EATBANG HTTP CONNECTION";

	private static EatBangConnection instance;
	private DefaultHttpClient httpClient;
	private String serverIP;
	private int serverPort;
	private HttpHost targetHost;
	private HttpRequestBase httpMethod;

	private EatBangConnection() {
		initialize();
	}

	private void initialize() {
		serverIP = PropertyManager.getInstance().getProperty("serverIP");
		serverPort = PropertyManager.getInstance().getIntProperty("serverPort",
				DEFAULT_PORT);
		targetHost = new HttpHost(serverIP, serverPort, "http");
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		httpClient = new DefaultHttpClient(params);
	}

	public static EatBangConnection getInstance() {
		if (instance == null) {
			instance = new EatBangConnection();
		}
		return instance;
	}

	public InputStream connect(String url, String method,
			List<NameValuePair> nameValuePairs) throws Exception {
		if ("get".equalsIgnoreCase(method)) {
			httpMethod = new HttpGet(url);
		} else if ("post".equalsIgnoreCase(method)) {
			httpMethod = new HttpPost(url);
			((HttpPost) httpMethod).setEntity(new UrlEncodedFormEntity(
					nameValuePairs, "UTF-8"));
			if ("/login".equalsIgnoreCase(url)) {
				httpMethod.addHeader("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
			}
		} else if ("put".equalsIgnoreCase(method)) {
			httpMethod = new HttpPut(url);
			((HttpPut) httpMethod).setEntity(new UrlEncodedFormEntity(
					nameValuePairs));
		} else if ("delete".equalsIgnoreCase(method)) {
			httpMethod = new HttpDelete(url);
		}

		httpMethod.addHeader("Accept", "application/json");

		String requestStr = httpMethod.getRequestLine().toString();
		Log.i(LOG_TAG, "To target: " + targetHost);
		Log.i(LOG_TAG, "Request String: " + requestStr);

		HttpResponse response = httpClient.execute(targetHost, httpMethod);
		HttpEntity entity = response.getEntity();

		Log.i(LOG_TAG, "Response status: " + response.getStatusLine());

		if (entity != null) {
			Log.i(LOG_TAG,
					"Response content length: " + entity.getContentLength());
			Log.i(LOG_TAG, "Response content type: "
					+ entity.getContentType().getName() + "  "
					+ entity.getContentType().getValue());
		}
		InputStream is = entity.getContent();
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources.

		return is;
	}
}
