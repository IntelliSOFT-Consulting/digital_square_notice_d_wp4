package org.openmrs.module.hei.atomfeed.client.api.worker;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.HttpHeaders;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.hei.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hei.atomfeed.client.api.util.PatientUrlUtil;


public class HieAtomFeedEventWorker implements EventWorker {
	
	private Log log = LogFactory.getLog(HieAtomFeedEventWorker.class);
	
	private HieAtomFeedProperties properties;
	
	private HttpClient httpClient;
	
	private PatientService patientService;
	
	private PatientTranslator patientTranslator;
	
	public HieAtomFeedEventWorker(HttpClient httpClient, HieAtomFeedProperties properties, PatientService patientService,
	    PatientTranslator patientTranslator) {
		this.properties = properties;
		this.httpClient = httpClient;
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
	}
	
	@Override
	public void process(Event event) {
		String content = event.getContent();
		if (content == null || "".equals(content)) {
			log.error("No content in event: " + event);
			return;
		}
		if (event.getTitle().equals("Patient")) {
			processPatientEvent(event);
		}
		
	}
	
	private void processPatientEvent(Event event) {
		String patientUrl = properties.getOpenMrsUri() + event.getContent();
		String patientUuid = PatientUrlUtil.getFulUuidVarFromUrl(event.getContent());

		try {
			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				log.error("HeiAtom feed error : Could not get patient with uuid " + patientUuid);
				return;
			}
			org.hl7.fhir.r4.model.Patient hl7Patient = patientTranslator.toFhirResource(patient);
			
			Gson gson = new Gson();
			String fhirJson = gson.toJson(hl7Patient);

			//TODO : Push fhir json object to hie server
			
		}
		catch (JsonParseException e) {
			log.error(String.format("Error parsing event %s with error %s", event.toString(), e.toString()));
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private HttpHeaders getHttpHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.put("Content-Type", "application/fhir+json");
		httpHeaders.put("Accept-Charset", "utf-8");
		httpHeaders.put("Accept", "application/fhir+json");
		
		return httpHeaders;
	}
	
	@Override
	public void cleanUp(Event event) {
		
	}
}
