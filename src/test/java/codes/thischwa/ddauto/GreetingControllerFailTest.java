package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class }, properties = { "ddauto.greeting-enabled=false" })
class GreetingControllerFailTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void greetingShouldReturnDefaultMessage() throws URISyntaxException {
		String url = "http://localhost:" + port + "/";
		assertEquals(HttpStatus.UNAUTHORIZED, restTemplate.getForEntity(new URI(url), String.class).getStatusCode());
	}
}
