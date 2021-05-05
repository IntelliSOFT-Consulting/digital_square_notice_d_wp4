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
	
	private String fhirServerPatientEndpoint = "http://45.33.84.72:5001/fhir/patient";
	
	private String fhirServerObservationEndpoint = "http://45.33.84.72:5001/fhir/observation";
	
	private String fhirServerEncounterEndpoint = "http://45.33.84.72:5001/fhir/encounter";
	
	private String getDefaultObsConcept = "856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	private String fhirHostUser = "fhir";
	
	private String fhirHostPassword = "12345";
	
	private List<String> encounterTypes = new ArrayList<String>();
	
	private int feedMaxFailedEvents = 10000;
	
	private int feedConnectionTimeoutInMilliseconds = 10000;
	
	private int feedReplyTimeoutInMilliseconds = 20000;
	
	private String fhirServerLocationEndpoint = "http://45.33.84.72:5001/fhir/location";
	
	private String fhirServerPractitionerEndpoint = "http://45.33.84.72:5001/fhir/practitioner";
	
	private String fhirServerServiceRequestEndpoint = "http://45.33.84.72:5001/fhir/serviceRequest";
	
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
		encounterTypes.add("LAB_RESULT");
		return encounterTypes;
	}
	
	public String getFhirServerPatientEndpoint() {
		return fhirServerPatientEndpoint;
	}
	
	public String getFhirHostUser() {
		return fhirHostUser;
	}
	
	public String getFhirHostPassword() {
		return fhirHostPassword;
	}
	
	public String getFhirServerObservationEndpoint() {
		return fhirServerObservationEndpoint;
	}
	
	public String getFhirServerEncounterEndpoint() {
		return fhirServerEncounterEndpoint;
	}
	
	public String getFhirServerLocationEndpoint() {
		return fhirServerLocationEndpoint;
	}
	
	public String getFhirServerPractitionerEndpoint() {
		return fhirServerPractitionerEndpoint;
	}
	
	public String getFhirServerServiceRequestEndpoint() {
		return fhirServerServiceRequestEndpoint;
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
