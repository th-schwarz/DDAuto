package codes.thischwa.ddauto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Holds and validates the data at start.
 */
@Component
public class DDAutoContext {

	private static final Logger logger = LoggerFactory.getLogger(DDAutoContext.class);

	@Autowired
	private DDAutoConfig config;

	// <zone, ns>
	private Map<String, String> zoneData = null;

	// <fqdn, apitoken>
	private Map<String, String> apitokenData = null;
	
	public Set<String> getConfiguredHosts() {
		return apitokenData.keySet();
	}

	public Set<String> getConfiguredZones() {
		return zoneData.keySet();
	}
	
	@PostConstruct
	void init() {
		readAndValidateData();
		logger.info("*** Api-token and zone data are read and validated successful!");
	}

	void readAndValidateData() {
		readData();
		validateData();
	}

	public boolean hostExists(String host) {
		return apitokenData.containsKey(host);
	}
	
	public String getApitoken(String host) throws IllegalArgumentException {
		if(!hostExists(host))
			throw new IllegalArgumentException("Host isn't configured: " + host);
		return apitokenData.get(host);
	}
	
	public String getPrimaryNameServer(String zone) throws IllegalArgumentException {
		if(!zoneData.containsKey(zone))
			throw new IllegalArgumentException("Zone isn't configured: " + zone);
		return zoneData.get(zone);
	}

	void readData() throws IllegalArgumentException {
		apitokenData = new HashMap<>();
		zoneData = new HashMap<>();
		for(DDAutoConfig.Zone zone : config.getZones()) {
			zoneData.put(zone.getName(), zone.getNs());
			List<String> hostRawData = zone.getHosts();
			if(hostRawData == null || hostRawData.isEmpty())
				throw new IllegalArgumentException("Missing host data for: " + zone.getName());
			for(String hostRaw : hostRawData) {
				String[] parts = hostRaw.split(":");
				if(parts.length != 2)
					throw new IllegalArgumentException("The host entry must be in the following format: [sld|:[apitoken], but it was: " + hostRaw);
				// build the fqdn hosstname
				String host = String.format("%s.%s", parts[0], zone.getName());
				apitokenData.put(host, parts[1]);
			}
		}
	}

	void validateData() {
		if(zoneData == null || zoneData.isEmpty() || apitokenData == null || apitokenData.isEmpty())
			throw new IllegalArgumentException("Zone or host data are empty.");
		logger.info("*** Configured hosts:");
		for(String host : apitokenData.keySet()) {
			logger.info(" - {}", host);
		}
	}
}
