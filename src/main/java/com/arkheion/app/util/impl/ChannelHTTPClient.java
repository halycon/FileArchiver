package com.arkheion.app.util.impl;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

public abstract class ChannelHTTPClient implements InitializingBean {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private HttpClient client;
	private List<BasicHeader> defaultHeaders = null;

	private int requestTimeout = 60 * 1000; // 60 sn.
	private int connectionTimeout = 10000; // 10 sn
	private int socketTimeout = 60000; // 60 sn

	@Override
	public void afterPropertiesSet() throws Exception {

		HttpProcessorBuilder builder = HttpProcessorBuilder.create();

		builder.add(new RequestAcceptEncoding());
		builder.add(new RequestConnControl());
		builder.add(new RequestTargetHost());
		builder.add(new ResponseContentEncoding());
		builder.add(new RequestContent());
		builder.add(new RequestClientConnControl());

		if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
			List<Header> headers = new ArrayList<Header>(defaultHeaders.size());
			for (Header header : defaultHeaders) {
				headers.add(header);
			}
			builder.add(new RequestDefaultHeaders(headers));
		}

		HttpProcessor processor = builder.build();

		SSLConnectionSocketFactory socketFactory = null;

		socketFactory = SSLConnectionSocketFactory.getSocketFactory();

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", socketFactory).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setDefaultMaxPerRoute(200);
		cm.setMaxTotal(400);

		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setConnectTimeout(connectionTimeout);
		requestBuilder = requestBuilder.setSocketTimeout(socketTimeout);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(requestTimeout);

		HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build());

		clientBuilder.setConnectionManager(cm);
		clientBuilder.setHttpProcessor(processor);

		
//		if (true) {			
//			HttpHost proxy = new HttpHost("10.0.0.2", 8081, "http");
//			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//			client = clientBuilder.setRoutePlanner(routePlanner).build();
//		} else
			client = clientBuilder.build();

	}

	protected String getContent(String request) {

		HttpGet get = new HttpGet(request);

		String content = null;

		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				content = EntityUtils.toString(entity);
			} else {
				logger.error("::getContent  post :" + request + " status:" + statusLine);
			}
		} catch (Exception e) {
			logger.error("::getSessionId  ", e);

		} finally {
			get.releaseConnection();
		}

		return content;
	}

	protected String postContent(String url, String request) {

		HttpPost post = new HttpPost(url);
		String content = null;

		try {

			StringEntity strent = new StringEntity(request, "UTF-8");
			strent.setContentType("application/xml");
			post.setEntity(strent);

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				content = EntityUtils.toString(entity);
			} else {
				logger.error("::postContent  post :" + request + " status:" + statusLine);
			}
		} catch (Exception e) {
			logger.error("::postContent  ", e);

		} finally {
			post.releaseConnection();
		}

		return content;
	}

	protected boolean isConnectionException(String message) {
		return message != null
				&& (message.contains("Connection reset") || message.contains("Connection refused") || message.contains("connect timed out")
						|| message.contains("Read timed out") || message.contains("Remote host closed connection"));
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

}
