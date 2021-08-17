package codes.thischwa.ddauto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codes.thischwa.ddauto.service.ZoneSdk;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * The starter and config.
 */
@SpringBootApplication
@Configuration
public class DDAutoStarter {

	private static final Logger logger = LoggerFactory.getLogger(DDAutoStarter.class);

	private static String CONST_SWAGGER_ENABLED_CLI = "--swagger.enabled=true";

	@Value("${zone.validation.enabled:true}")
	private boolean zoneValidation;

	@Autowired
	private ZoneSdk sdk;

	public static void main(String[] args) {
		try {
			SpringApplication app = new SpringApplication(DDAutoStarter.class);
			List<String> cmdArgs = new ArrayList<>(Arrays.asList(args));
			if(!cmdArgs.remove(CONST_SWAGGER_ENABLED_CLI)) {
				logger.info("*** springdoc disabled!");
				cmdArgs.add("--springdoc.api-docs.enabled=false");
			} else {
				logger.info("*** springdoc enabled!");
			}
			app.run(cmdArgs.toArray(new String[0]));
		} catch (Exception e) {
			System.err.println("Unexpected exception, Spring Boot stops!");
			System.exit(10);
		}
	}

	/**
	 * Creates a listener for the ApplicationReadyEvent, that validates the configured zones.
	 * 
	 * @return the required ApplicationListener
	 */
	@Bean
	ApplicationListener<ApplicationReadyEvent> createApplicationReadyListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {

			@Override
			public void onApplicationEvent(ApplicationReadyEvent event) {
				if(zoneValidation) {
					logger.debug("Will be process zone-validation ...");
					sdk.validateConfiguredZones();
				} else {
					logger.debug("Zone validation isn't set.");
				}
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
