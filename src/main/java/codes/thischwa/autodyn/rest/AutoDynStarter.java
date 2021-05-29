package codes.thischwa.autodyn.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The starter and config.
 */
@SpringBootApplication
@Configuration
public class AutoDynStarter {

	@Autowired
	private Context context;

	@Autowired
	private DomainrobotSdk sdk;
	
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
