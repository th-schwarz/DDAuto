package codes.thischwa.ddauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The starter and config.
 */
@SpringBootApplication
public class DDAutoStarter {

	
	public static void main(String[] args) {
		try {
			SpringApplication.run(DDAutoStarter.class, CommandlineArgsProcessor.process(args).toArray(new String[0]));
		} catch (Exception e) {
			System.err.println("Unexpected exception, Spring Boot stops! Message: " + e.getMessage());
			//System.exit(10);
		}
	}

}
