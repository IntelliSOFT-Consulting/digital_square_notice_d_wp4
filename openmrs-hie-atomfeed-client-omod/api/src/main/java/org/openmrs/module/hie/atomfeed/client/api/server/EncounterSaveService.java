package org.openmrs.module.hie.atomfeed.client.api.server;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.joda.time.DateTime;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class EncounterSaveService implements AfterReturningAdvice {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private final AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	private static final String DEFAULT_LAB_ENCOUNTER_TYPE = "LAB_RESULT";
	
	private static final String SAVE_METHOD = "saveEncounter";
	
	public static final String TITLE = "Lab Encounter";
	
	public static final String CATEGORY = "Encounter";
	
	private EventService eventService;
	
	public EncounterSaveService() throws SQLException {
		PlatformTransactionManager platformTransactionManager = getSpringPlatformTransactionManager();
		atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(platformTransactionManager);
		//AllEventRecordsJdbcImpl records = new AllEventRecordsJdbcImpl(new OpenMRSConnectionProvider(platformTransactionManager));
		AllEventRecordsQueue allEventRecordsQueue = new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager);
		this.eventService = new EventServiceImpl(allEventRecordsQueue);
	}
	
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] objects, Object o1) throws Throwable {
		if (method.getName().equals(SAVE_METHOD)
		        && ((Encounter) returnValue).getEncounterType().getName().equals(DEFAULT_LAB_ENCOUNTER_TYPE)) {
			log.error("\n Running on save method \n");
			Object encounterUuid = PropertyUtils.getProperty(returnValue, "encounterUuid");
			String url = String.format(getEncounterFeedUrl(), encounterUuid);
			final Event event = new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), (URI) null, url, CATEGORY);
			if (returnValue instanceof Encounter
			        && ((Encounter) returnValue).getEncounterType().getName().equals(DEFAULT_LAB_ENCOUNTER_TYPE)) {
				atomFeedSpringTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
					
					@Override
					protected void doInTransaction() {
						eventService.notify(event);
					}
					
					@Override
					public PropagationDefinition getTxPropagationDefinition() {
						return PropagationDefinition.PROPAGATION_REQUIRED;
					}
				});
			} else {
				log.error("Could not process after event");
			}
		}
		
	}
	
	private static String getEncounterFeedUrl() {
		return Context.getAdministrationService().getGlobalProperty("encounter.feed.publish.url");
	}
	
	private PlatformTransactionManager getSpringPlatformTransactionManager() {
		List<PlatformTransactionManager> platformTransactionManagers = Context
		        .getRegisteredComponents(PlatformTransactionManager.class);
		return platformTransactionManagers.get(0);
	}
}
