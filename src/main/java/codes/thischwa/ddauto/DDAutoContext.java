package codes.thischwa.ddauto;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * Holds the data and validates it at start.
 */
@Component
public class DDAutoContext {

	private static final Logger logger = LoggerFactory.getLogger(DDAutoContext.class);

	@Value("${dir.data}")
	private String dataDir;

	@Value("${data.zone.name}")
	private String zoneDataName;

	@Value("${data.apitoken.name}")
	private String apitokenDataName;

	private Properties zoneData = null;

	private Properties apitokenData = null;
	
	public Set<String> getConfiguredHosts() {
		return apitokenData.stringPropertyNames();
	}

	public Set<String> getConfiguredZones() {
		return zoneData.stringPropertyNames();
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
		return apitokenData.getProperty(host);
	}
	
	public String getPrimaryNameServer(String zone) throws IllegalArgumentException {
		if(!zoneData.containsKey(zone))
			throw new IllegalArgumentException("Zone isn't configured: " + zone);
		return zoneData.getProperty(zone);
	}

	private void readData() {
		try {
			zoneData = readPropertiesfromDataDir(zoneDataName);
			apitokenData = readPropertiesfromDataDir(apitokenDataName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void validateData(Properties zoneData, Properties apitokenData) {
		if(zoneData == null || apitokenData == null || apitokenData.isEmpty())
			throw new IllegalArgumentException("Account and zone data are inconsistent.");
		for(String host : apitokenData.stringPropertyNames()) {
			String domain = host.substring(host.indexOf(".") + 1);
			if(!zoneData.containsKey(domain))
				throw new IllegalArgumentException("Missing zone data for: " + domain);
		}
	}

	private Properties readPropertiesfromDataDir(String fileName) throws IOException {
		Path path = Paths.get(dataDir, fileName);
		return PropertiesLoaderUtils.loadProperties(new FileSystemResource(path));
	}
}
