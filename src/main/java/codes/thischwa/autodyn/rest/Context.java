package codes.thischwa.autodyn.rest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * Holds the data and validates it at start.
 */
@Component
public class Context {

	private static final Logger logger = LoggerFactory.getLogger(Context.class);
	
	@Value("${dir.data}")
	private String dataDir;
	
	@Autowired
	private DomainrobotSdk sdk;
	
	private Properties zoneData = null;

	private Properties accountData = null;

	public Properties getZoneData() {
		return zoneData;
	}

	public Properties getAccountData() {
		return accountData;
	}

	public void readAndValidateData() {
		readData();
		validateData(zoneData, accountData);
		for(String z : zoneData.stringPropertyNames()) {
			Zone zone = sdk.getZone(z,  zoneData.getProperty(z));
			logger.info("Zone correct initialized: {}", zone.getOrigin());
		}
	}
	
	public boolean hostExists(String host) {
		return accountData.containsKey(host);
	}
	
	private void readData() {
		try {
			zoneData = readPropertiesfromDataDir("zone.properties");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			accountData = readPropertiesfromDataDir("account.properties");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void validateData(Properties zoneData, Properties accountData) {
		if(zoneData == null || accountData == null || (accountData.size() != zoneData.size()))
			throw new IllegalArgumentException("Account and zone data are inconsistent.");
		for(String sld : accountData.stringPropertyNames()) {
			String domain = sld.substring(sld.indexOf(".") + 1);
			if(!zoneData.containsKey(domain))
				throw new IllegalArgumentException("No zone data found for: " + domain);
		}
	}

	private Properties readPropertiesfromDataDir(String fileName) throws IOException {
		Path path = Paths.get(dataDir, fileName);
		return PropertiesLoaderUtils.loadProperties(new FileSystemResource(path));
	}
}
