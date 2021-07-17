package codes.thischwa.ddauto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codes.thischwa.ddauto.service.ZoneSdk;

/**
 * The starter and config.
 */
@SpringBootApplication
@Configuration
public class DDAutoStarter {

	@Autowired
	private ZoneSdk sdk;

	public static void main(String[] args) {
		try {
			SpringApplication.run(DDAutoStarter.class, args);
		} catch (Exception e) {
			System.out.println("Unexpected exception, Spring Boot stops!");
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
				sdk.validateConfiguredZones();
			}
		};
	}
}
