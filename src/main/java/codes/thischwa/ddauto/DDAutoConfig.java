package codes.thischwa.ddauto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ddauto")
public class DDAutoConfig {

	@NotEmpty(message = "The zones of the AutoDNS configuration shouldn't be empty.")
	private List<Zone> zones;

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<@Valid Zone> zones) {
		this.zones = zones;
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
