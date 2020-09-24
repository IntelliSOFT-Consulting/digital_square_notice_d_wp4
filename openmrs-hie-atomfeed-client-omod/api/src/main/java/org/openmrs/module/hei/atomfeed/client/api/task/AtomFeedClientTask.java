package org.openmrs.module.hei.atomfeed.client.api.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.hei.atomfeed.client.api.client.OpenMrsPatientFeedClient;
import org.openmrs.scheduler.tasks.AbstractTask;

public class AtomFeedClientTask extends AbstractTask {
	
	Log log = LogFactory.getLog(AtomFeedClientTask.class);
	
	@Override
	public void execute() {
		runTask();
	}
	
	private void runTask() {
		OpenMrsPatientFeedClient feedClient = Context.getService(OpenMrsPatientFeedClient.class);
		feedClient.processFeed();
	}
}
