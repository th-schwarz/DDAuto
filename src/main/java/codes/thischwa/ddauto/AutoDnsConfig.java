package codes.thischwa.ddauto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Holds the AutoDNS credentials for the Domainrobot Sdk.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "autodns")
public class AutoDnsConfig {

	private String url;
	private int context;
	private String user;
	private String password;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getContext() {
		return context;
	}

	public void setContext(int context) {
		this.context = context;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
