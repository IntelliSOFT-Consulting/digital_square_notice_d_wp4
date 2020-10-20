package org.openmrs.module.hei.atomfeed.util;

import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.FhirServerStoreUtil;

import java.io.IOException;

public class FhireStoreUtilTest {
	
	@Test
	public void testSendFhirJson() throws Exception {
		String fhirJson = "{\"name\": \"Upendra\", \"job\": \"Programmer\"}";
		//StatusLine statusLine = FhirServerStoreUtil.withHttp("fhir", "12345", fhirJson, "http://45.33.84.72:5001/fhir");
		//StatusLine statusLine = FhirServerStoreUtil.postFhirResource(new HieAtomFeedProperties(), fhirJson);
		//Assert.assertEquals(404, statusLine.getStatusCode());
		FhirServerStoreUtil util = new FhirServerStoreUtil();
		//util.exportPatient(getPatient(), new HieAtomFeedProperties());
	}
	
	private Patient getPatient() {
		Patient newPatient = new Patient();
		
		// Populate the patient with fake information
		newPatient.addName().setFamily("DevDays2015").addGiven("John").addGiven("Q");
		newPatient.addIdentifier().setSystem("http://acme.org/mrn").setValue("1234567");
		newPatient.setGender(Enumerations.AdministrativeGender.MALE);
		newPatient.setBirthDateElement(new DateType("2015-11-18"));
		
		return newPatient;
	}
	
}
