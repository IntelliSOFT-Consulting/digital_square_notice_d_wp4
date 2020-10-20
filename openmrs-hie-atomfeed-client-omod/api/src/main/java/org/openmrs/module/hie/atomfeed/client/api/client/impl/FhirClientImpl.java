package org.openmrs.module.hie.atomfeed.client.api.client.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.client.FhirClient;
import org.openmrs.module.hie.atomfeed.client.api.exception.HieClientExcepption;
import org.springframework.stereotype.Component;

@Component("hieFhirClient")
public class FhirClientImpl extends BaseOpenmrsService implements FhirClient {
	
	private Log log = LogFactory.getLog(FhirClientImpl.class);
	
	@Override
	public void exportPatient(Patient hl7Patient, HieAtomFeedProperties properties) throws HieClientExcepption {
		try {
			IGenericClient client = this.getClient(properties);
			log.error("Posting " + hl7Patient);
			MethodOutcome result = client.create().resource(hl7Patient).execute();
			if (!result.getCreated()) {
				log.error("Problems");
				log.error(result);
				throw new HieClientExcepption(String.format("FHIR Server error :> %s", result.getResource().getClass()
				        .getName()));
			} else {
				log.error("POsted");
				log.error(result.getCreated());
				log.error(result.getResource().getMeta());
			}
			
		}
		catch (HieClientExcepption e) {
			log.error("Error in FHIR PIX message", e);
			e.printStackTrace();
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new HieClientExcepption(e);
		}
		finally {}
	}
	
	@Override
	public void updatePatient(Patient hhlPatient, HieAtomFeedProperties properties) throws HieClientExcepption {
		
	}
	
	@Override
	public void exportEncounter(Encounter encounter) throws HieClientExcepption {
		
	}
	
	@Override
	public void exportObservation(Observation observation) throws HieClientExcepption {
		
	}
	
	private IGenericClient getClient(HieAtomFeedProperties properties) {
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient(properties.getFhirServerHost());
		client.setEncoding(EncodingEnum.JSON);
		fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		client.registerInterceptor(new BasicAuthInterceptor(properties.getFhirHostUser(), properties.getFhirHostPassword()));
		
		return client;
	}
}
