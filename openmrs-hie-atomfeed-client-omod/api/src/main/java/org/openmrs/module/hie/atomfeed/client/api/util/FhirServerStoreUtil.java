package org.openmrs.module.hie.atomfeed.client.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.openmrs.api.APIException;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.exception.HieClientExcepption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FhirServerStoreUtil {
	
	private static Log log = LogFactory.getLog(FhirServerStoreUtil.class);
	
	public static StatusLine withHttp(String user, String password, String fhirJsonesource, String fhirServerUrl)
	        throws IOException, AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(fhirServerUrl);
		
		httpPost.setEntity(new StringEntity(fhirJsonesource));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
		httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
		
		CloseableHttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		client.close();
		return statusLine;
	}
	
	public static StatusLine postFhirResource(HieAtomFeedProperties properties, String fhirJsonesource,
	        ResourceType resourceType) throws IOException, AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPut;
		
		if (resourceType.equals(ResourceType.Patient)) {
			httpPut = new HttpPost(properties.getFhirServerPatientEndpoint());
		} else if (resourceType.equals(ResourceType.Observation)) {
			httpPut = new HttpPost(properties.getFhirServerObservationEndpoint());
		} else if (resourceType.equals(ResourceType.Encounter)) {
			httpPut = new HttpPost(properties.getFhirServerEncounterEndpoint());
		} else {
			httpPut = new HttpPost(properties.getFhirServerHost());
		}

		httpPut.addHeader("Content-Type", "application/fhir+json");
		httpPut.setEntity(new StringEntity(fhirJsonesource));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(properties.getFhirHostUser(),
		        properties.getFhirHostPassword());
		httpPut.addHeader(new BasicScheme().authenticate(creds, httpPut, null));
		
		CloseableHttpResponse response = client.execute(httpPut);
		StatusLine statusLine = response.getStatusLine();
		log.error("Status returned " + statusLine.getStatusCode());
		log.error(statusLine.getReasonPhrase());
		client.close();
		return statusLine;
	}
	
	public static StatusLine putFhirResource(HieAtomFeedProperties properties, String fhirJsonResource,
	        ResourceType resourceType, String resourceId) throws IOException, AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut;
		
		if (resourceType.equals(ResourceType.Patient)) {
			httpPut = new HttpPut(properties.getFhirServerPatientEndpoint() + "/" + resourceId);
		} else if (resourceType.equals(ResourceType.Observation)) {
			httpPut = new HttpPut(properties.getFhirServerObservationEndpoint() + "/" + resourceId);
		} else if (resourceType.equals(ResourceType.Encounter)) {
			httpPut = new HttpPut(properties.getFhirServerEncounterEndpoint() + "/" + resourceId);
		} else if (resourceType.equals(ResourceType.Location)) {
			httpPut = new HttpPut(properties.getFhirServerLocationEndpoint() + "/" + resourceId);
		} else if (resourceType.equals(ResourceType.Practitioner)) {
			httpPut = new HttpPut(properties.getFhirServerPractitionerEndpoint() + "/" + resourceId);
		} else if (resourceType.equals(ResourceType.ServiceRequest)) {
			httpPut = new HttpPut(properties.getFhirServerServiceRequestEndpoint() + "/" + resourceId);
		} else {
			throw new APIException("Unsupported resource type");
		}
		
		//fhirJsonResource = addResourceTypeToJson(fhirJsonResource, resourceType);
		log.debug("Resource type: " + resourceType + ": " + fhirJsonResource);
		httpPut.addHeader("Content-Type", "application/fhir+json");
		httpPut.setEntity(new StringEntity(fhirJsonResource));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(properties.getFhirHostUser(),
		        properties.getFhirHostPassword());
		httpPut.addHeader(new BasicScheme().authenticate(creds, httpPut, null));
		
		CloseableHttpResponse response = client.execute(httpPut);
		StatusLine statusLine = response.getStatusLine();
		log.error("Status returned " + statusLine.getStatusCode());
		log.error(statusLine.getReasonPhrase());
		client.close();
		return statusLine;
	}
	
	private IGenericClient getClient(HieAtomFeedProperties properties) {
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient(properties.getFhirServerPatientEndpoint());
		client.setEncoding(EncodingEnum.JSON);
		fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		client.registerInterceptor(new BasicAuthInterceptor(properties.getFhirHostUser(), properties.getFhirHostPassword()));
		
		return client;
	}
	
	/**
	 * Post FHIR patient resource to server .
	 * 
	 * @param fhirPatient the @{@link Patient} FHIR patient instance to post
	 * @param properties initialized @{@link HieAtomFeedProperties} instance
	 */
	public Patient exportPatient(Patient fhirPatient, HieAtomFeedProperties properties) throws Exception {
        try {
            IGenericClient client = this.getClient(properties);
            log.error("Posting " + fhirPatient);


            Bundle bundle = new Bundle();
            List<Bundle.BundleEntryComponent> bundleEntryComponents = new ArrayList<>();

            Bundle.BundleEntryComponent first_entry = new Bundle.BundleEntryComponent();
            first_entry.setFullUrl(fhirPatient.toString() + fhirPatient.getResourceType().name() + fhirPatient.getId());
            first_entry.setResource(fhirPatient);

            bundleEntryComponents.add(first_entry);
            bundle.setEntry(bundleEntryComponents);


            MethodOutcome result = client.create().resource(bundle).prettyPrint().encodedJson().execute();
            if (!result.getCreated()) {
                throw new Exception(String.format("FHIR Server error :> %s", result.getResource().getClass().getName()));
            } else {
                log.error(result.getCreated());
                log.error(result.getResource().getMeta());
            }

        } catch (HieClientExcepption e) {
            log.error("Error in FHIR PIX message", e);
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new HieClientExcepption(e);
        } finally {
        }
        return fhirPatient;
    }
	
	private static String addResourceTypeToJson(String json, ResourceType resourceType) {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		jsonObject.addProperty("resourceType", StringUtils.capitalize(resourceType.getPath()));
		if (jsonObject.has("contained")) {
			JsonArray jsonArray = jsonObject.getAsJsonArray("contained");
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObject.getAsJsonArray("contained").get(i).getAsJsonObject()
				        .addProperty("resourceType", StringUtils.capitalize(ResourceType.Bundle.getPath()));
			}
		}
		
		return new Gson().toJson(jsonObject);
	}
}
