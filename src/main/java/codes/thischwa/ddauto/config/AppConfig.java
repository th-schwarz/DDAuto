package codes.thischwa.ddauto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * 'The' configuration class.
 */
@Configuration
public class AppConfig {

	@Bean
	public OpenAPI customizeOpenAPI() {
		return new OpenAPI().components(new Components())
				.externalDocs(new ExternalDocumentation().description("DDAuto on Github").url("https://github.com/th-schwarz/DDAuto"))
				.info(new Info().title("DDAuto :: Dynamic DNS with AutoDNS").description("The routes of the dynamic DNS API").version("1.0")
						.license(new License().name("MIT").url("https://github.com/th-schwarz/DDAuto/blob/develop/LICENSE")));
	}

}
