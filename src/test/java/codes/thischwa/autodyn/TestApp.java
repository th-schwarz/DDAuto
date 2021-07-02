package codes.thischwa.autodyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class TestApp {

	public static void main(String[] args) {
		SpringApplication.run(AutoDynStarter.class, args);
	}
	
	@Bean
	ApplicationListener<ApplicationReadyEvent> createApplicationReadyListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			
			@Override
			public void onApplicationEvent(ApplicationReadyEvent event) {				
			}
		};
	}
}
