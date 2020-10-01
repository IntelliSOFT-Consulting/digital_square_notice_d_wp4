package org.openmrs.module.hei.atomfeed.util;

import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.FhirServerStoreUtil;

import java.io.IOException;

public class FhireStoreUtilTest {
	
	@Test
	public void testSendFhirJson() throws IOException, AuthenticationException {
		String fhirJson = "{\"name\": \"Upendra\", \"job\": \"Programmer\"}";
		//StatusLine statusLine = FhirServerStoreUtil.withHttp("fhir", "12345", fhirJson, "http://45.33.84.72:5001/fhir");
		StatusLine statusLine = FhirServerStoreUtil.postFhirResource(new HieAtomFeedProperties(), fhirJson);
		Assert.assertEquals(404, statusLine.getStatusCode());
	}
}
