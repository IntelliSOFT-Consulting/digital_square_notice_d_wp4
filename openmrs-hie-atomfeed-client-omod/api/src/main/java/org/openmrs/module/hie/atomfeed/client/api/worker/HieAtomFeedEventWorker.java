package org.openmrs.module.hie.atomfeed.client.api.worker;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.HttpHeaders;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.fhir2.api.translators.impl.EncounterTranslatorImpl;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.PatientUrlUtil;

public class HieAtomFeedEventWorker implements EventWorker {
	
	private Log log = LogFactory.getLog(HieAtomFeedEventWorker.class);
	
	private HieAtomFeedProperties properties;
	
	private HttpClient httpClient;
	
	private Gson gson;
	
	private PatientService patientService;
	
	private PatientTranslator patientTranslator;
	
	private EncounterTranslatorImpl encounterTranslator;
	
	private EncounterService encounterService;
	
	public HieAtomFeedEventWorker(HttpClient httpClient, HieAtomFeedProperties properties, PatientService patientService,
	    PatientTranslator patientTranslator, EncounterTranslatorImpl encounterTranslator, EncounterService encounterService) {
		this.properties = properties;
		this.httpClient = httpClient;
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
		this.encounterTranslator = encounterTranslator;
		this.encounterService = encounterService;
		this.gson = new Gson();
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
		} else if (event.getTitle().equals(properties.getLabResultEventTitle())) {
			processEncounterFeed(event);
		}
		
	}
	
	private void processPatientEvent(Event event) {
		String patientUrl = properties.getOpenMrsUri() + event.getContent();
		String patientUuid = PatientUrlUtil.getFulUuidVarFromUrl(event.getContent());
		
		try {
			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				log.error("HieAtom feed error : Could not get patient with uuid " + patientUuid);
				return;
			}
			org.hl7.fhir.r4.model.Patient hl7Patient = patientTranslator.toFhirResource(patient);
			String fhirJson = gson.toJson(hl7Patient);
			log.error(fhirJson);
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
	
	private void processEncounterFeed(Event event) {
		log.info("Processing Encounter Feed");
		String encounterUuid = PatientUrlUtil.getFulUuidVarFromUrl(event.getContent());
		log.error("Encounter uuid " + encounterUuid);
		Encounter encounter = encounterService.getEncounterByUuid(encounterUuid);
		if (encounter == null) {
			log.error("Hie Atom feed error : Can not get encounter with uuid - " + encounterUuid);
			return;
		}
		log.error("Encounter type " + encounter.getEncounterType().getName());
		if (!properties.getEncounterTypes().contains(encounter.getEncounterType().getName())) {
			log.error("Hie Atom feed : Skipping encounter tyoe- " + encounter.getEncounterType().getName());
			return;
		}
		
		org.hl7.fhir.r4.model.Encounter hl7Encounter = encounterTranslator.toFhirResource(encounter);
		String fhirJson = gson.toJson(hl7Encounter);
		log.error(fhirJson);
		
		//TODO : Push fhir resource to Hie endpoint
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
