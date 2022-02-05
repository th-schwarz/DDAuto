package codes.thischwa.ddauto.service;

import java.util.HashMap;
import java.util.Map;

import org.domainrobot.sdk.client.Domainrobot;
import org.domainrobot.sdk.client.clients.ZoneClient;
import org.domainrobot.sdk.models.DomainRobotHeaders;
import org.domainrobot.sdk.models.DomainrobotApiException;
import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codes.thischwa.ddauto.config.AutoDnsConfig;
import codes.thischwa.ddauto.config.DDAutoConfig;
import codes.thischwa.ddauto.util.ZoneUtil;

/**
 * A simple wrapper to ZoneClient of the Domainrobot Sdk.
 */
@Service
public class ZoneSdk {

	private static final Logger logger = LoggerFactory.getLogger(ZoneSdk.class);

	private static final Map<String, String> customHeaders = new HashMap<>(Map.of(DomainRobotHeaders.DOMAINROBOT_HEADER_WEBSOCKET, "NONE"));

	@Autowired
	private AutoDnsConfig autoDnsConfig;

	@Autowired
	private DDAutoConfig config;

	private ZoneClient getInstance() {
		return new Domainrobot(autoDnsConfig.getUser(), String.valueOf(autoDnsConfig.getContext()), autoDnsConfig.getPassword(),
				autoDnsConfig.getUrl()).getZone();
	}

	public void validateConfiguredZones() {
		for(String z : config.getConfiguredZones()) {
			Zone zone = zoneInfo(z, config.getPrimaryNameServer(z));
			logger.info("Zone correct initialized: {}", zone.getOrigin());
		}
	}

	Zone zoneInfo(String origin, String primaryNameServer) throws ZoneSdkException {
		ZoneClient zc = getInstance();
		try {
			return zc.info(origin, primaryNameServer, customHeaders);
		} catch (DomainrobotApiException e) {
			throw new ZoneSdkException("API exception", e);
		} catch (Exception e) {
			throw new ZoneSdkException("Unknown exception", e);
		}
	}

	public Zone zoneInfo(String host) throws ZoneSdkException, IllegalArgumentException {
		if(!config.hostExists(host))
			throw new IllegalArgumentException("Host isn't configured: " + host);
		String zone = ZoneUtil.deriveZone(host);
		String primaryNameServer = config.getPrimaryNameServer(zone);
		return zoneInfo(zone, primaryNameServer);
	}

	/**
	 * Updates the zone derived from the host. <br>
	 * The parameters must be validated by the caller.
	 * 
	 * @param host
	 *            the hostname, should be a sub domain
	 * @param ipv4
	 *            Add or update the ipv4 address. If it's null, it will be dropped from the zone.
	 * @param ipv6
	 *            Add or update the ipv6 address. If it's null, it will be dropped from the zone.
	 * @throws ZoneSdkException
	 *            If an known exception DomainrobotApiException or an unknown exception was happened.
	 */
	public void zoneUpdate(String host, String ipv4, String ipv6) throws ZoneSdkException {
		String sld = host.substring(0, host.indexOf("."));

		// set the IPs in the zone object
		Zone zone = zoneInfo(host);
		if(ipv4 != null)
			ZoneUtil.addOrUpdateIPv4(zone, sld, ipv4);
		else
			ZoneUtil.removeIPv4(zone, sld);
		if(ipv6 != null)
			ZoneUtil.addOrUpdateIPv6(zone, sld, ipv6);
		else
			ZoneUtil.removeIPv6(zone, sld);

		// processing the update
		ZoneClient zc = getInstance();
		try {
			zc.update(zone, customHeaders);
		} catch (DomainrobotApiException e) {
			throw new ZoneSdkException("API exception", e);
		} catch (Exception e) {
			throw new ZoneSdkException("Unknown exception", e);
		}
	}
}
