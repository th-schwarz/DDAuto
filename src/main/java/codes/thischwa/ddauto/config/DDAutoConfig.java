package codes.thischwa.ddauto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ddauto")
public class DDAutoConfig {

	private String zoneLogFilePattern;

	private boolean zoneLogPageEnabled;

	private String zoneLogPattern = "(.*)\\s+-\\s+([a-zA-Z\\.-]*)\\s+(\\S*)\\s+(\\S*)";

	private String zoneLogDatePattern = "yyyy-MM-dd HH:mm:SSS";

	private boolean zoneValidationEnabled = true;
	
	private String zoneLogUserName;

	private String zoneLogUserPassword;
	
	public String getZoneLogFilePattern() {
		return zoneLogFilePattern;
	}

	public boolean isZoneLogPageEnabled() {
		return zoneLogPageEnabled;
	}

	public void setZoneLogPageEnabled(boolean logPageEnabled) {
		this.zoneLogPageEnabled = logPageEnabled;
	}

	public String getZoneLogPattern() {
		return zoneLogPattern;
	}

	public void setZoneLogPattern(String zoneLogPattern) {
		this.zoneLogPattern = zoneLogPattern;
	}

	public String getZoneLogDatePattern() {
		return zoneLogDatePattern;
	}

	public void setZoneLogDatePattern(String zoneLogDatePattern) {
		this.zoneLogDatePattern = zoneLogDatePattern;
	}

	public void setZoneLogFilePattern(String zoneLogFilePattern) {
		this.zoneLogFilePattern = zoneLogFilePattern;
	}

	public boolean isZoneValidationEnabled() {
		return zoneValidationEnabled;
	}

	public void setZoneValidationEnabled(boolean zoneValidationEnabled) {
		this.zoneValidationEnabled = zoneValidationEnabled;
	}

	public String getZoneLogUserName() {
		return zoneLogUserName;
	}

	public void setZoneLogUserName(String zoneLogUserName) {
		this.zoneLogUserName = zoneLogUserName;
	}

	public String getZoneLogUserPassword() {
		return zoneLogUserPassword;
	}

	public void setZoneLogUserPassword(String zoneLogUserPassword) {
		this.zoneLogUserPassword = zoneLogUserPassword;
	}

}
