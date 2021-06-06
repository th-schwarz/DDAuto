package codes.thischwa.autodyn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codes.thischwa.autodyn.service.ZoneSdk;

/**
 * The starter and config.
 */
@SpringBootApplication
@Configuration
public class AutoDynStarter {

	@Autowired
	private AuoDynContext context;

	@Autowired
	private ZoneSdk sdk;
	
	public static void main(String[] args) {
		try {
			SpringApplication.run(AutoDynStarter.class, args);
		} catch (Exception e) {
            System.out.println("Unexpected exception, Spring Boot stops!");
            System.exit(10);
		}
	}
	
	@Bean
	ApplicationListener<ApplicationReadyEvent> createApplicationReadyListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			
			@Override
			public void onApplicationEvent(ApplicationReadyEvent event) {
				context.readAndValidateData();	
				sdk.checkConfiguredZones();
			}
		};
	}
}
