package codes.thischwa.ddauto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ddauto")
public class DDAutoConfig {

	private List<Zone> zones;

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}

	public static class Zone {

		@NotBlank
		private String name;

		@NotBlank
		private String ns;

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

		public void setHosts(List<String> host) {
			this.hosts = host;
		}

	}
}
