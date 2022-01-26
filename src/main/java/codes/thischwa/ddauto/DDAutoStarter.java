package codes.thischwa.ddauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * The starter and config.
 */
@SpringBootApplication
@Configuration
public class DDAutoStarter {

	
	public static void main(String[] args) {
		try {
			SpringApplication app = new SpringApplication(DDAutoStarter.class);
			app.run(CommandlineArgsProcessor.process(args).toArray(new String[0]));
		} catch (Exception e) {
			System.err.println("Unexpected exception, Spring Boot stops! Message: " + e.getMessage());
			//System.exit(10);
		}
	}

}
