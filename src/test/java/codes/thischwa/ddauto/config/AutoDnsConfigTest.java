package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.GenericIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoDnsConfigTest extends GenericIntegrationTest {
	
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
