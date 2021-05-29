package codes.thischwa.autodyn.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

	@Autowired
	private Context context;
	
	@Bean
	ApplicationListener<ApplicationReadyEvent> createApplicationReadyListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			
			@Override
			public void onApplicationEvent(ApplicationReadyEvent event) {
				context.readAndValidateData();	
				
			}
		};
	}
}
