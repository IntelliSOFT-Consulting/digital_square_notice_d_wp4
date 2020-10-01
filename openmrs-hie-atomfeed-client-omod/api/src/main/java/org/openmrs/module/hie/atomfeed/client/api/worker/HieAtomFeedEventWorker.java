package org.openmrs.module.hie.atomfeed.client.api.worker;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.HttpHeaders;
import org.hl7.fhir.r4.model.Observation;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.translators.ObservationTranslator;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.fhir2.api.translators.impl.EncounterTranslatorImpl;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.FhirServerStoreUtil;
import org.openmrs.module.hie.atomfeed.client.api.util.PatientUrlUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HieAtomFeedEventWorker implements EventWorker {
	
	private Log log = LogFactory.getLog(HieAtomFeedEventWorker.class);
	
	private HieAtomFeedProperties properties;
	
	private HttpClient httpClient;
	
	private Gson gson;
	
	private PatientService patientService;
	
	private PatientTranslator patientTranslator;
	
	private EncounterTranslatorImpl encounterTranslator;
	
	private ObservationTranslator observationTranslator;
	
	private EncounterService encounterService;
	
	public HieAtomFeedEventWorker(HttpClient httpClient, HieAtomFeedProperties properties, PatientService patientService,
	    PatientTranslator patientTranslator, EncounterTranslatorImpl encounterTranslator, EncounterService encounterService,
	    ObservationTranslator observationTranslator) {
		this.properties = properties;
		this.httpClient = httpClient;
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
		this.encounterTranslator = encounterTranslator;
		this.encounterService = encounterService;
		this.observationTranslator = observationTranslator;
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
			postFhirResource(fhirJson);
			
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
		
		postFhirResource(fhirJson);
		
		Set<Obs> obsHashSet = encounter.getObs();
		HashSet<String> observationHashSet = new HashSet<String>();
		for (Obs obs : obsHashSet) {
			if (obs.getConcept().getUuid().equals(properties.getGetDefaultObsConcept())) {
				
				Observation hl7Observation = observationTranslator.toFhirResource(obs);
				String obsFhirJson = gson.toJson(hl7Observation);
				log.error(obsFhirJson);
				observationHashSet.add(obsFhirJson);
				
			}
		}
		
		for (String obsFhirResource : observationHashSet) {
			postFhirResource(obsFhirResource);
		}
	}
	
	private void postFhirResource(String fhirResource) {
        try {
            StatusLine statusLine = FhirServerStoreUtil.postFhirResource(properties, fhirResource);
            if (statusLine.getStatusCode() != 200) {
                log.error("FHIR Server error : " + statusLine.getStatusCode() + " \n Error message " + statusLine.getReasonPhrase());
            }
        } catch (IOException | AuthenticationException e) {
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
