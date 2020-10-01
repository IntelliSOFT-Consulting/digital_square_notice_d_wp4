package org.openmrs.module.hie.atomfeed.client.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;

import java.io.IOException;

public class FhirServerStoreUtil {
	
	private static Log log = LogFactory.getLog(FhirServerStoreUtil.class);
	
	public static StatusLine withHttp(String user, String password, String fhirJsonesource, String fhirServerUrl)
	        throws IOException, AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(fhirServerUrl);
		
		httpPost.setEntity(new StringEntity(fhirJsonesource));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
		httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
		
		CloseableHttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		client.close();
		return statusLine;
	}
	
	public static StatusLine postFhirResource(HieAtomFeedProperties properties, String fhirJsonesource) throws IOException,
	        AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(properties.getFhirServerHost());
		
		httpPost.setEntity(new StringEntity(fhirJsonesource));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(HieAtomFeedProperties.fhirHostUser,
		        HieAtomFeedProperties.fhirHostPassword);
		httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
		
		CloseableHttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		client.close();
		return statusLine;
	}
}
