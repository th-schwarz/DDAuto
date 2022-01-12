package codes.thischwa.ddauto;

import java.util.List;

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
			List<String> cmdArgs = CommandlineArgsProcessor.process(args);
			app.run(cmdArgs.toArray(new String[cmdArgs.size()]));
		} catch (Exception e) {
			System.err.println("Unexpected exception, Spring Boot stops! Message: " + e.getMessage());
			//System.exit(10);
		}
	}


}
