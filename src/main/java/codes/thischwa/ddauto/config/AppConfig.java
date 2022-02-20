package codes.thischwa.ddauto.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codes.thischwa.ddauto.service.ZoneSdk;
import codes.thischwa.ddauto.service.ZoneUpdateCache;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * 'The' configuration class.
 */
@Configuration
public class AppConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	@Value("${zone.validation.enabled:true}")
	private boolean zoneValidation;

	/**
	 * Creates a listener for the ApplicationReadyEvent, that validates the configured zones and prefills the cache.
	 * 
	 * @return the required ApplicationListener
	 */
	@Bean
	ApplicationListener<ApplicationReadyEvent> createApplicationReadyListener(ZoneSdk sdk, ZoneUpdateCache cache) {
		return e -> {
			if(zoneValidation) {
				logger.debug("Process zone-validation ...");
				sdk.validateConfiguredZones();
			} else {
				logger.debug("Zone validation isn't set, no validation processed.");
			}
		};
	}

	@Bean
	public OpenAPI customizeOpenAPI() {
		return new OpenAPI().components(new Components())
				.externalDocs(new ExternalDocumentation().description("DDAuto on Github").url("https://github.com/th-schwarz/DDAuto"))
				.info(new Info().title("DDAuto :: Dynamic DNS with AutoDNS").description("The routes of the dynamic DNS API").version("1.0")
						.license(new License().name("MIT").url("https://github.com/th-schwarz/DDAuto/blob/develop/LICENSE")));
	}

}
