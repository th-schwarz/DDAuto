package codes.thischwa.ddauto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The starter and config.
 */
@SpringBootApplication
public class DDAutoStarter {

	private static final Logger logger = LoggerFactory.getLogger(DDAutoStarter.class);
	
	public static void main(String[] args) {
		try {
			SpringApplication.run(DDAutoStarter.class, CommandlineArgsProcessor.process(args).toArray(new String[0]));
		} catch (Exception e) {
			logger.error("Unexpected exception, Spring Boot stops! Message: {}", e.getMessage());
			System.exit(10);
		}
	}

}
