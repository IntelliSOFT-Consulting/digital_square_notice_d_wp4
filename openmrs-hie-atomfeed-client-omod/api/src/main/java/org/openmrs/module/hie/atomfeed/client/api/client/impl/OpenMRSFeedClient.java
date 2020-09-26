package org.openmrs.module.hie.atomfeed.client.api.client.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.hie.atomfeed.client.api.HieAtomFeedProperties;
import org.openmrs.module.hie.atomfeed.client.api.util.FeedNames;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class OpenMRSFeedClient {
	
	protected AtomFeedClient atomFeedClient;
	
	private PlatformTransactionManager transactionManager;
	
	private Log logger = LogFactory.getLog(OpenMRSFeedClient.class);
	
	private HieAtomFeedProperties properties;
	
	private List<AtomFeedClient> feedClients = new ArrayList<AtomFeedClient>();
	
	public OpenMRSFeedClient(HieAtomFeedProperties properties, PlatformTransactionManager transactionManager) {
		this.properties = properties;
		this.transactionManager = transactionManager;
	}
	
	/**
	 * @param feedUri
	 * @return
	 * @throws java.lang.RuntimeException if feed Uri is invalid
	 */
	private URI getURIForFeed(String feedUri) {
		try {
			return new URI(feedUri);
		}
		catch (URISyntaxException e) {
			logger.error("heiatomfeedclient:error instantiating client:" + e.getMessage(), e);
			throw new RuntimeException("Invalid Uri :" + feedUri);
		}
	}
	
	public org.ict4h.atomfeed.client.service.FeedClient getAtomFeedClient() {
		if (atomFeedClient == null) {
			createAtomFeedClient();
		}
		return atomFeedClient;
	}
	
	public List<AtomFeedClient> getAtomFeedClients() {
		if (feedClients == null || feedClients.size() == 0) {
			feedClients.add(createAtomFeedClient(FeedNames.PATIENT_FEED_TYPE));
			feedClients.add(createAtomFeedClient(FeedNames.ENCOUNTER_FEED_TYPE));
		}
		return feedClients;
	}
	
	public org.ict4h.atomfeed.client.service.FeedClient createAtomFeedClient() {
		URI uriForFeed = getURIForFeed(getFeedUri(properties));
		ConnectionDetails connectionDetails = createConnectionDetails(properties);
		HttpClient httpClient = new HttpClient(connectionDetails, new OpenMRSLoginAuthenticator(connectionDetails));
		ClientCookies cookies = httpClient.getCookies(uriForFeed);
		EventWorker openMRSEventWorker = createWorker(httpClient, properties);
		AtomFeedSpringTransactionManager txMgr = new AtomFeedSpringTransactionManager(transactionManager);
		atomFeedClient = new AtomFeedClient(new AllFeeds(properties, cookies), new AllMarkersJdbcImpl(txMgr),
		        new AllFailedEventsJdbcImpl(txMgr), properties, txMgr, uriForFeed, openMRSEventWorker);
		return atomFeedClient;
	}
	
	public AtomFeedClient createAtomFeedClient(String feedType) {
		URI uriForFeed = getURIForFeed(getFeedUri(properties, feedType));
		ConnectionDetails connectionDetails = createConnectionDetails(properties);
		HttpClient httpClient = new HttpClient(connectionDetails, new OpenMRSLoginAuthenticator(connectionDetails));
		ClientCookies cookies = httpClient.getCookies(uriForFeed);
		EventWorker openMRSEventWorker = createWorker(httpClient, properties);
		AtomFeedSpringTransactionManager txMgr = new AtomFeedSpringTransactionManager(transactionManager);
		return new AtomFeedClient(new AllFeeds(properties, cookies), new AllMarkersJdbcImpl(txMgr),
		        new AllFailedEventsJdbcImpl(txMgr), properties, txMgr, uriForFeed, openMRSEventWorker);
	}
	
	private ConnectionDetails createConnectionDetails(HieAtomFeedProperties properties) {
		return new ConnectionDetails(properties.getOpenmrsAuthUri(), properties.getOpenmrsUser(),
		        properties.getOpenMrsPassword(), properties.getConnectTimeout(), properties.getReadTimeout());
	}
	
	protected abstract String getFeedUri(HieAtomFeedProperties properties);
	
	protected abstract String getFeedUri(HieAtomFeedProperties properties, String type);
	
	protected abstract EventWorker createWorker(HttpClient authenticatedWebClient, HieAtomFeedProperties properties);
	
}
