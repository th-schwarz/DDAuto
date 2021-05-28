package codes.thischwa.autodyn.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The starter.
 */
@SpringBootApplication
public class AutoDynStarter {

	public static void main(String[] args) {
		try {
			SpringApplication.run(AutoDynStarter.class, args);
		} catch (Exception e) {
            System.out.println("Unexpected exception, Spring Boot stops!");
            System.exit(10);
		}
	}

}
