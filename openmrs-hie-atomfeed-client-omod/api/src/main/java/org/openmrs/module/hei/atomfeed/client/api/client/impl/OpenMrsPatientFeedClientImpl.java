package org.openmrs.module.hei.atomfeed.client.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.hei.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hei.atomfeed.client.api.client.OpenMrsPatientFeedClient;
import org.openmrs.module.hei.atomfeed.client.api.helper.HieHttpHelper;
import org.openmrs.module.hei.atomfeed.client.api.worker.HieAtomFeedEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Component("openMrsPatientFeedClient")
public class OpenMrsPatientFeedClientImpl extends OpenMRSFeedClient implements OpenMrsPatientFeedClient {
	
	private Log log = LogFactory.getLog(OpenMrsPatientFeedClientImpl.class);
	
	private PatientService patientService;
	
	private PatientTranslator patientTranslator;
	
	@Autowired
	public OpenMrsPatientFeedClientImpl(HieAtomFeedProperties properties, PlatformTransactionManager transactionManager,
	    PatientService patientService, PatientTranslator patientTranslator) {
		super(properties, transactionManager);
		this.patientService = patientService;
		this.patientTranslator = patientTranslator;
	}
	
	@Override
	public void processFeed() {
		try {
			log.error("HieOpenmrsClientFeedImpl: processing feed " + DateTime.now());
			getAtomFeedClient().processEvents();
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
	protected EventWorker createWorker(HttpClient authenticatedWebClient, HieAtomFeedProperties properties) {
		return new HieAtomFeedEventWorker(authenticatedWebClient, properties, patientService, patientTranslator);
	}
	
	private boolean isUnauthorised(Exception e) {
		return ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")
		        || ExceptionUtils.getStackTrace(e).contains("HTTP response code: 403")
		        || ExceptionUtils.getStackTrace(e).contains("User not authorized");
	}
}
