package codes.thischwa.autodyn;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
public class AutoDynContext {

	private static final Logger logger = LoggerFactory.getLogger(AutoDynContext.class);
	@Value("${dir.data}")
	private String dataDir;
	
	private Properties zoneData = null;

	private Properties accountData = null;

	public Properties getZoneData() {
		return zoneData;
	}

	public Properties getAccountData() {
		return accountData;
	}

	@PostConstruct
	void init() {
		readAndValidateData();
		logger.info("*** Account and zone data read and validated successful!");
	}
	
	void readAndValidateData() {
		readData();
		validateData(zoneData, accountData);
	}
	
	public boolean hostExists(String host) {
		return accountData.containsKey(host);
	}
	
	private void readData() {
		try {
			zoneData = readPropertiesfromDataDir("zone.properties");
			accountData = readPropertiesfromDataDir("account.properties");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void validateData(Properties zoneData, Properties accountData) {
		if(zoneData == null || accountData == null || accountData.isEmpty())
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
