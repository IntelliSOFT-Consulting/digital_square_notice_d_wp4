package org.openmrs.module.hie.atomfeed.client.api.client;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.exception.HieClientExcepption;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = HieClientExcepption.class)
public interface FhirClient extends OpenmrsService {
	
	public void exportPatient(Patient hl7Patient, HieAtomFeedProperties properties) throws HieClientExcepption;
	
	public void updatePatient(Patient hl7Patient, HieAtomFeedProperties properties) throws HieClientExcepption;
	
	public void exportEncounter(Encounter encounter) throws HieClientExcepption;
	
	public void exportObservation(Observation observation) throws HieClientExcepption;
}
