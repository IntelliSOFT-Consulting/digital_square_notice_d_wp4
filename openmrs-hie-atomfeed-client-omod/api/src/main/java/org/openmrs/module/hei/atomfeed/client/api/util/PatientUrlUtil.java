package org.openmrs.module.hei.atomfeed.client.api.util;

public class PatientUrlUtil {
	
	public static String getFulUuidVarFromUrl(String url) {
		String result = url.substring(url.lastIndexOf("/") + 1);
		if (result.contains("?")) {
			{
				result = result.split("\\?")[0];
			}
		}
		return result;
	}
	
	public static String getPatientFhirResourceUrl(String fhirBaseUri, String patientUuid) {
		return String.format("%s/Patient/%s", fhirBaseUri, patientUuid);
	}
}
