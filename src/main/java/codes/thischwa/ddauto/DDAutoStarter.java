package codes.thischwa.ddauto;

import java.util.ArrayList;
import java.util.Arrays;
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

	private static final String const_swagger_enabled_cli = "--swagger.enabled=true";
	
	public static void main(String[] args) {
		try {
			SpringApplication app = new SpringApplication(DDAutoStarter.class);

			List<String> cmdArgs = new ArrayList<>(Arrays.asList(args));
			// map swagger property
			if(!cmdArgs.remove(const_swagger_enabled_cli)) 
				cmdArgs.add("--springdoc.api-docs.enabled=false");
			
			app.run(cmdArgs.toArray(new String[cmdArgs.size()]));
		} catch (Exception e) {
			System.err.println("Unexpected exception, Spring Boot stops! Message: " + e.getMessage());
			//System.exit(10);
		}
	}


}
