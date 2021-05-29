package codes.thischwa.autodyn.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.domainrobot.sdk.client.Domainrobot;
import org.domainrobot.sdk.client.clients.ZoneClient;
import org.domainrobot.sdk.models.DomainRobotHeaders;
import org.domainrobot.sdk.models.DomainrobotApiException;
import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DomainrobotSdk {

	private static final Logger logger = LoggerFactory.getLogger(Context.class);

	@Value("${autodns.url}")
	private String baseUrl;

	@Value("${autodns.user}")
	private String user;

	@Value("${autodns.password}")
	private String password;

	@Value("${autodns.context}")
	private String autodnsContext;

	@Autowired
	private Context context;

	private ZoneClient getInstance() {
		return new Domainrobot(user, autodnsContext, password, baseUrl).getZone();
	}

	private Map<String, String> getCustomHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put(DomainRobotHeaders.DOMAINROBOT_HEADER_WEBSOCKET, "NONE");
		return headers;
	}

	void checkConfiguredZones() {
		Properties zoneData = context.getZoneData();
		for(String z : zoneData.stringPropertyNames()) {
			Zone zone = getZone(z,  zoneData.getProperty(z));
			logger.info("Zone correct initialized: {}", zone.getOrigin());
		}
	}

	Zone getZone(String origin, String primaryNameServer) throws SdkException {
		ZoneClient zc = getInstance();
		try {
			return zc.info(origin, primaryNameServer, getCustomHeaders());
		} catch (DomainrobotApiException e) {
			throw new SdkException("API exception", e);
		} catch (Exception e) {
			throw new SdkException("Unknown exception", e);
		}
	}

	public Zone getZoneOfHost(String host) throws SdkException {
		if(!context.getAccountData().containsKey(host))
			throw new IllegalArgumentException("Host isn't configured: " + host);
		String zone = identifyZone(host);
		String primaryNameServer = context.getZoneData().getProperty(zone);
		return getZone(zone, primaryNameServer);
	}

	public void updateZone(String host, String ipv4, String ipv6) throws SdkException {
		// params must be validated by the caller
		String sld = host.substring(0, host.indexOf("."));
		Zone zone = getZoneOfHost(host);
		if(ipv4 != null)
			ZoneUtil.addOrUpdateIPv4(zone, sld, ipv4);
		if(ipv6 != null)
			ZoneUtil.addOrUpdateIPv6(zone, sld, ipv6);
		ZoneClient zc = getInstance();
		try {
			zc.update(zone, getCustomHeaders());
		} catch (DomainrobotApiException e) {
			throw new SdkException("API exception", e);
		} catch (Exception e) {
			throw new SdkException("Unknown exception", e);
		}
	}

	String identifyZone(String host) {
		return host.substring(host.indexOf(".") + 1);
	}
}
