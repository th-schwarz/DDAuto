package codes.thischwa.ddauto.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ddauto")
public class DDAutoConfig implements InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(DDAutoConfig.class);

	// <zone, ns>
	private Map<String, String> zoneData = null;

	// <fqdn, apitoken>
	private Map<String, String> apitokenData = null;

	@NotEmpty(message = "The zones of the AutoDNS configuration shouldn't be empty.")
	private List<Zone> zones;

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<@Valid Zone> zones) {
		this.zones = zones;
	}

	public Set<String> getConfiguredHosts() {
		return apitokenData.keySet();
	}

	public Set<String> getConfiguredZones() {
		return zoneData.keySet();
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

	@Override
	public void afterPropertiesSet() throws Exception {
		readAndValidate();
		logger.info("*** Api-token and zone data are read and validated successful!");
	}

	void readAndValidate() {
		read();
		validate();
	}
	
	void read() throws IllegalArgumentException {
		apitokenData = new HashMap<>();
		zoneData = new HashMap<>();
		for(DDAutoConfig.Zone zone : zones) {
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

	void validate() {
		if(zoneData == null || zoneData.isEmpty() || apitokenData == null || apitokenData.isEmpty())
			throw new IllegalArgumentException("Zone or host data are empty.");
		logger.info("*** Configured hosts:");
		for(String host : apitokenData.keySet()) {
			logger.info(" - {}", host);
		}
	}
	
	public static class Zone {

		@NotBlank(message = "The name of the zone shouldn't be empty.")
		private String name;

		@NotBlank(message = "The primary name server of the zone shouldn't be empty.")
		private String ns;

		// is validated by DDAutoContext#readData
		private List<String> hosts;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNs() {
			return ns;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

		public List<String> getHosts() {
			return hosts;
		}

		public void setHosts(@Valid List<String> host) {
			this.hosts = host;
		}

	}
}
