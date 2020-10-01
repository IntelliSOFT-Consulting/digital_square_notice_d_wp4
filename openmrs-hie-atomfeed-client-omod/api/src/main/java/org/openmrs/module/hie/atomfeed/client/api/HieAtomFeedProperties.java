package org.openmrs.module.hie.atomfeed.client.api;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HieAtomFeedProperties extends AtomFeedProperties {
	
	//  TODO : Read properties from properties file or global properties
	
	private String openMrsUri = "http://localhost:8050";
	
	private String fhirBaseUri = "http://localhost:8050/openmrs/ws/fhir2/R4";
	
	private String patientFeedUri = "http://localhost:8050/openmrs/ws/atomfeed/patient/recent";
	
	private String encounterFeedUri = "http://localhost:8050/openmrs/ws/atomfeed/encounter/recent";
	
	private String openmrsAuthUri = "http://localhost:8050/openmrs/ws/rest/v1/session";
	
	private String openmrsUser = "admin";
	
	private String openMrsPassword = "test";
	
	private String labResultEventTitle = "Lab Encounter";
	
	private String fhirServerHost = "http://45.33.84.72:5001/fhir";
	
	private String getDefaultObsConcept = "fe769568-16da-4d9e-9c99-fbed0a8a60f5";
	
	public static final String fhirHostUser = "fhir";
	
	public static final String fhirHostPassword = "12345";
	
	private List<String> encounterTypes = new ArrayList<String>();
	
	private int feedMaxFailedEvents = 10000;
	
	private int feedConnectionTimeoutInMilliseconds = 10000;
	
	private int feedReplyTimeoutInMilliseconds = 20000;
	
	public String getOpenMrsUri() {
		return openMrsUri;
	}
	
	public String getFhirBaseUri() {
		return fhirBaseUri;
	}
	
	public String getPatientFeedUri() {
		return patientFeedUri;
	}
	
	public String getEncounterFeedUri() {
		return encounterFeedUri;
	}
	
	public String getOpenmrsAuthUri() {
		return openmrsAuthUri;
	}
	
	public String getOpenmrsUser() {
		return openmrsUser;
	}
	
	public String getOpenMrsPassword() {
		return openMrsPassword;
	}
	
	public String getGetDefaultObsConcept() {
		return getDefaultObsConcept;
	}
	
	public String getFhirServerHost() {
		return fhirServerHost;
	}
	
	public String getLabResultEventTitle() {
		return labResultEventTitle;
	}
	
	public List<String> getEncounterTypes() {
		encounterTypes.add("Consultation");
		encounterTypes.add("LAB_RESULT");
		return encounterTypes;
	}
	
	@Override
	public int getMaxFailedEvents() {
		return feedMaxFailedEvents;
	}
	
	@Override
	public int getReadTimeout() {
		return feedReplyTimeoutInMilliseconds;
	}
	
	@Override
	public int getConnectTimeout() {
		return feedConnectionTimeoutInMilliseconds;
	}
}
