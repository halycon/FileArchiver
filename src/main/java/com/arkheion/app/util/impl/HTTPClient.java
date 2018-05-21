package com.arkheion.app.util.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.arkheion.app.util.IHttpClient;
import com.google.gson.Gson;

@Service
public class HTTPClient extends ChannelHTTPClient implements IHttpClient {
	private Logger LOG = LoggerFactory.getLogger(getClass());
	private final static String HTTP_CONTENT_TYPE = "application/json;";
	private final static Gson gson = new Gson();

	@Override
	public String post(String postData, String url,String contentType) {
		HttpPost post = new HttpPost(url);
		String content = null;

		try {

			StringEntity strent = new StringEntity(postData, "UTF-8");
			strent.setContentType(contentType);
			post.setEntity(strent);
			post.setHeader("Content-Type", contentType);
//			post.addHeader("SOAPAction", soapAction);

			HttpResponse response = getClient().execute(post);
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				content = EntityUtils.toString(entity);
				LOG.warn("::postContent  post :{} status:{} content:{}", postData, statusLine, content);
			} else {
				String errorContent = EntityUtils.toString(entity);
				content = errorContent;
				LOG.error("::postContent  post :{}  \n status:{} \n errorContent:{} \n header:{}", postData, statusLine, errorContent, gson.toJson(post.getAllHeaders()));
			}
		} catch (Exception e) {
//			LOG.error("::postContent  ", e);

		} finally {
			post.releaseConnection();
		}


		return content;

	}

	

	public static String getHttpContentType() {
		return HTTP_CONTENT_TYPE;
	}

}
