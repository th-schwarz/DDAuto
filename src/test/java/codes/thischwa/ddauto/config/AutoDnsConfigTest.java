package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.DDAutoStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { AutoDnsConfig.class, DDAutoStarter.class })
class AutoDnsConfigTest {
	
	@Autowired
	private AutoDnsConfig config;

	@Test
	final void testGetUrl() {
		assertEquals("https://api.autodns.com/v1", config.getUrl());
	}

	@Test
	final void testGetContext() {
		assertEquals(4, config.getContext());
	}

	@Test
	final void testGetUser() {
		assertEquals("user_t", config.getUser());
	}

	@Test
	final void testGetPassword() {
		assertEquals("pwd_t", config.getPassword());
	}

}
