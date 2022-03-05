package codes.thischwa.ddauto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DDAutoConfig {

	@Value("${ddauto.zone.log-file-pattern}")
	private String zoneLogFilePattern;

	@Value("${ddauto.zone.log-page.enabled:true}")
	private boolean logPageEnabled;

	@Value("${ddautozone.log-pattern:(.*)\\s+-\\s+([a-zA-Z\\.-]*)\\s+(\\S*)\\s+(\\S*)}")
	private String zoneLogPattern;

	@Value("${ddauto.zone.log-date-pattern:yyyy-MM-dd HH:mm:SSS}")
	private String zoneLogDatePattern;

	@Value("${ddauto.zone.validation.enabled:true}")
	private boolean zoneValidationEnabled;
	
	public String getZoneLogFilePattern() {
		return zoneLogFilePattern;
	}

	public boolean isLogPageEnabled() {
		return logPageEnabled;
	}

	public void setLogPageEnabled(boolean logPageEnabled) {
		this.logPageEnabled = logPageEnabled;
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

}
