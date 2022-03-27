package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import codes.thischwa.ddauto.util.NetUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class })
@ExtendWith(SpringExtension.class)
class GreetingControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void greetingShouldReturnDefaultMessage() {
		assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/", String.class).contains("DDAuto"));
	}
}
