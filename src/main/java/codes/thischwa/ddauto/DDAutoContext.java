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
 * Holds the data and validates it at start.
 */
@Component
public class DDAutoContext {

	private static final Logger logger = LoggerFactory.getLogger(DDAutoContext.class);

	@Autowired
	private DDAutoConfig config;

	private Map<String, String> zoneData = null;

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
		validateData(zoneData, apitokenData);
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

	void readData() {
		apitokenData = new HashMap<>();
		zoneData = new HashMap<>();
		for(DDAutoConfig.Zone zone : config.getZones()) {
			zoneData.put(zone.getName(), zone.getNs());
			List<String> hostRawData = zone.getHosts();
			if(hostRawData == null || hostRawData.isEmpty())
				throw new IllegalArgumentException("Missing host data for: " + zone.getName());
			for(String hostRaw : hostRawData) {
				if(!hostRaw.contains(":"))
					throw new IllegalArgumentException("The host entry must be in the following format: [sld|:[apitoken], but it was: " + hostRaw);
				String[] parts = hostRaw.split(":");
				if(parts.length != 2)
					throw new IllegalArgumentException("The host entry must be in the following format: [sld|:[apitoken], but it was: " + hostRaw);
				String host = String.format("%s.%s", parts[0], zone.getName());
				apitokenData.put(host, parts[1]);
			}
		}
	}

	void validateData(Map<String, String> zoneData, Map<String, String> apitokenData) {
		if(zoneData == null || apitokenData == null || apitokenData.isEmpty())
			throw new IllegalArgumentException("Account and zone data are inconsistent.");
		for(String host : apitokenData.keySet()) {
			String domain = host.substring(host.indexOf(".") + 1);
			if(!zoneData.containsKey(domain))
				throw new IllegalArgumentException("Missing zone data for: " + domain);
		}
	}
}
