package org.openmrs.module.hie.atomfeed.client.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir2.api.translators.ObservationTranslator;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.fhir2.api.translators.impl.EncounterTranslatorImpl;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.client.FhirClient;
import org.openmrs.module.hie.atomfeed.client.api.client.OpenMrsPatientFeedClient;
import org.openmrs.module.hie.atomfeed.client.api.util.FeedNames;
import org.openmrs.module.hie.atomfeed.client.api.util.FhirServerStoreUtil;
import org.openmrs.module.hie.atomfeed.client.api.worker.HieAtomFeedEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Component("openMrsPatientFeedClient")
public class OpenMrsPatientFeedClientImpl extends OpenMRSFeedClient implements OpenMrsPatientFeedClient {
	
	private Log log = LogFactory.getLog(OpenMrsPatientFeedClientImpl.class);
	
	private PatientService patientService;
	
	private PatientTranslator patientTranslator;
	
	private EncounterTranslatorImpl encounterTranslator;
	
	private EncounterService encounterService;
	
	private ObservationTranslator observationTranslator;
	
	@Autowired
	public OpenMrsPatientFeedClientImpl(HieAtomFeedProperties properties, PlatformTransactionManager transactionManager,
	    PatientService patientService, PatientTranslator patientTranslator, EncounterTranslatorImpl encounterTranslator,
	    EncounterService encounterService, ObservationTranslator observationTranslator, FhirClient fhirClient) {
		super(properties, transactionManager);
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
		this.encounterTranslator = encounterTranslator;
		this.encounterService = encounterService;
		this.observationTranslator = observationTranslator;
	}
	
	@Override
	public void processFeed() {
		try {
			getAtomFeedClient().processEvents();
			for (AtomFeedClient feedclient : getAtomFeedClients()) {
				feedclient.processEvents();
			}
		}
		catch (Exception e) {
			try {
				if (e != null && isUnauthorised(e)) {
					log.error(e.getMessage());
					log.error(Arrays.toString(e.getStackTrace()));
					createAtomFeedClient();
				}
			}
			catch (Exception ex) {
				log.error("HieOpenmrsClientFeed:failed feed execution " + e, e);
				throw new RuntimeException(ex);
			}
		}
	}
	
	@Override
	protected String getFeedUri(HieAtomFeedProperties properties) {
		return properties.getPatientFeedUri();
	}
	
	@Override
	protected String getFeedUri(HieAtomFeedProperties properties, String type) {
		if (type.equals(FeedNames.PATIENT_FEED_TYPE)) {
			return properties.getPatientFeedUri();
		} else if (type.equals(FeedNames.ENCOUNTER_FEED_TYPE)) {
			return properties.getEncounterFeedUri();
		}
		return null;
	}
	
	@Override
	protected EventWorker createWorker(HttpClient authenticatedWebClient, HieAtomFeedProperties properties) {
		return new HieAtomFeedEventWorker(authenticatedWebClient, properties, patientService, patientTranslator,
		        encounterTranslator, encounterService, observationTranslator);
	}
	
	private boolean isUnauthorised(Exception e) {
		return ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")
		        || ExceptionUtils.getStackTrace(e).contains("HTTP response code: 403")
		        || ExceptionUtils.getStackTrace(e).contains("User not authorized");
	}
}
