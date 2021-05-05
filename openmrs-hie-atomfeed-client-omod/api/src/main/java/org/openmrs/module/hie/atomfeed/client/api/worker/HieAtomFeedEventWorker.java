package org.openmrs.module.hie.atomfeed.client.api.worker;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.HttpHeaders;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.*;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.translators.*;
import org.openmrs.module.fhir2.api.translators.impl.*;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.FhirServerStoreUtil;
import org.openmrs.module.hie.atomfeed.client.api.util.PatientUrlUtil;

import ca.uhn.fhir.context.FhirContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	
	private LocationTranslator locationTranslator;
	
	private PractitionerTranslator practitionerTranslator;
	
	private VisitTranslatorImpl visitTranslator;
	
	private ServiceRequestTranslatorImpl serviceRequestTranslator;
	
	public HieAtomFeedEventWorker(HttpClient httpClient, HieAtomFeedProperties properties, PatientService patientService,
	    PatientTranslator patientTranslator, EncounterTranslatorImpl encounterTranslator, EncounterService encounterService,
	    ObservationTranslator observationTranslator, LocationTranslator locationTranslator,
	    VisitTranslatorImpl visitTranslator, PractitionerTranslatorProviderImpl practitionerTranslator,
	    ServiceRequestTranslatorImpl serviceRequestTranslator) {
		this.properties = properties;
		this.httpClient = httpClient;
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
		this.encounterTranslator = encounterTranslator;
		this.encounterService = encounterService;
		this.observationTranslator = observationTranslator;
		this.locationTranslator = locationTranslator;
		this.visitTranslator = visitTranslator;
		this.practitionerTranslator = practitionerTranslator;
		this.serviceRequestTranslator = serviceRequestTranslator;
		this.gson = new Gson();
	}
	
	@Override
	public void process(Event event) {
		log.debug("Processing has began");
		log.info(String.format("The current event is : %s", event.getTitle()));
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
		log.debug("Procesing patient event xxxxxxxxxxx");
		String patientUrl = properties.getOpenMrsUri() + event.getContent();
		String patientUuid = PatientUrlUtil.getFulUuidVarFromUrl(event.getContent());
		
		try {
			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				log.error("HieAtom feed error : Could not get patient with uuid " + patientUuid);
				return;
			}
			
			org.hl7.fhir.r4.model.Patient hl7Patient = patientTranslator.toFhirResource(patient);
			Set<PersonName> patientNames = patient.getNames();
			List<HumanName> humanNames = new ArrayList<HumanName>();
			
			for (PersonName personName : patientNames) {
				HumanName humanName = new HumanName();
				humanName.setFamily(personName.getFamilyName());
				humanName.addGiven(personName.getGivenName());
				if (personName.getMiddleName() != null) {
					humanName.addGiven(personName.getMiddleName());
				}
				
				if (personName.getPreferred() || patientNames.size() == 0) {
					humanName.setUse(NameUse.OFFICIAL);
				}
				
				humanNames.add(humanName);
			}
			
			Identifier systemIdentifier = new Identifier();
			CodeableConcept codingValue = new CodeableConcept();
			codingValue.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "PI",
			        "Patient External Identifier"));
			systemIdentifier.setType(codingValue);
			systemIdentifier.setSystem("LOCAL");
			//TODO: Concatenate the System  URL with the patient uuid: May be set it as a global config?
			systemIdentifier.setValue(patient.getUuid());
			
			hl7Patient.getIdentifier().add(systemIdentifier);
			hl7Patient.setName(humanNames);
			String fhirJson = convertResourceToJson(hl7Patient);
			putFhirResource(fhirJson, ResourceType.Patient, patient.getUuid());
			
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
			log.warn("Hie Atom feed error : Can not get encounter with uuid - " + encounterUuid);
			return;
		}
		log.error("Encounter type " + encounter.getEncounterType().getName());
		if (!properties.getEncounterTypes().contains(encounter.getEncounterType().getName())) {
			log.warn("Hie Atom feed : Skipping encounter type- " + encounter.getEncounterType().getName());
			return;
		}
		//Location
		Location encounterLocation = encounter.getLocation();
		if (encounterLocation != null) {
			// Dirty hack to prevent creation of a reference hierarchy
			// TODO: Replace this code block with a recursive method
			encounterLocation.setParentLocation(null);
			org.hl7.fhir.r4.model.Location hl7Location = locationTranslator.toFhirResource(encounterLocation);
			String locationFhirJson = convertResourceToJson(hl7Location);
			log.debug(locationFhirJson);
			putFhirResource(locationFhirJson, ResourceType.Location, encounterLocation.getUuid());
		}
		
		//Encounter participants
		Set<EncounterProvider> encounterProviders = encounter.getEncounterProviders();
		for (EncounterProvider encounterProvider : encounterProviders) {
			org.hl7.fhir.r4.model.Practitioner encounterParticipant = practitionerTranslator
			        .toFhirResource(encounterProvider.getProvider());
			if (encounterProvider.getProvider().getPerson().getGender() == null) {
				encounterParticipant.setGender(null);
			}
			String practitionerFhirJson = convertResourceToJson(encounterParticipant);
			log.debug(practitionerFhirJson);
			putFhirResource(practitionerFhirJson, ResourceType.Practitioner, encounterProvider.getProvider().getUuid());
		}
		
		//Visit
		Visit visit = encounter.getVisit();
		org.hl7.fhir.r4.model.Encounter hl7Visit = visitTranslator.toFhirResource(visit);
		String visitFhirJson = convertResourceToJson(hl7Visit);
		log.debug(visitFhirJson);
		putFhirResource(visitFhirJson, ResourceType.Encounter, visit.getUuid());
		
		//Encounter
		org.hl7.fhir.r4.model.Encounter hl7Encounter = encounterTranslator.toFhirResource(encounter);
		String fhirJson = convertResourceToJson(hl7Encounter);
		log.debug(fhirJson);
		putFhirResource(fhirJson, ResourceType.Encounter, encounterUuid);
		
		//Observation
		Set<Obs> obsHashSet = encounter.getObs();
		log.info("Logging Obs." + obsHashSet.size());
		for (Obs obs : obsHashSet) {
			log.info("Concept Id- " + obs.getConcept().getConceptId() + " ConceptName- " + obs.getConcept().getName());
			if (obs.getConcept().getUuid().equals(properties.getGetDefaultObsConcept())) {
				
				// Service request (To fulfill "BasedOn" attribute)
				TestOrder testOrder = new TestOrder();
				testOrder.setUuid(obs.getOrder().getUuid());
				ServiceRequest serviceRequest = serviceRequestTranslator.toFhirResource(testOrder);
				String serviceFhirJson = convertResourceToJson(serviceRequest);
				log.debug(serviceFhirJson);
				putFhirResource(serviceFhirJson, ResourceType.ServiceRequest, testOrder.getUuid());
				
				Observation hl7Observation = observationTranslator.toFhirResource(obs);
				String obsFhirJson = convertResourceToJson(hl7Observation);
				log.debug(obsFhirJson);
				
				putFhirResource(obsFhirJson, ResourceType.Observation, obs.getUuid());
			}
		}
	}
	
	private String convertResourceToJson(IBaseResource hl7Encounter) {
		return FhirContext.forR4().newJsonParser().encodeResourceToString(hl7Encounter);
	}
	
	private void postFhirResource(String fhirResource, ResourceType resourceType) {
		log.debug("Posting FHIR resource");
		try {
			StatusLine statusLine = FhirServerStoreUtil.postFhirResource(properties, fhirResource, resourceType);
			if (statusLine.getStatusCode() != 200) {
				log.error("FHIR Server error : " + statusLine.getStatusCode() + " \n Error message " + statusLine
						.getReasonPhrase());
			}
		}
		catch (IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}
	
	private void putFhirResource(String fhirResource, ResourceType resourceType, String resourceIdentifier) {
		log.debug("Posting FHIR resource");
		try {
			StatusLine statusLine = FhirServerStoreUtil
					.putFhirResource(properties, fhirResource, resourceType, resourceIdentifier);
			if (statusLine.getStatusCode() != 200) {
				log.error("FHIR Server error : " + statusLine.getStatusCode() + " \n Error message " + statusLine
						.getReasonPhrase());
			}
		}
		catch (IOException | AuthenticationException e) {
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
