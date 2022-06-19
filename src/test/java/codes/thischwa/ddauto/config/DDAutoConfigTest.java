package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.GenericIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class DDAutoConfigTest extends GenericIntegrationTest {

	@Autowired
	private DDAutoConfig config;

	@Test
	final void test() {
		assertTrue(config.isZoneLogPageEnabled());
		assertFalse(config.isZoneValidationEnabled());
		assertEquals("log", config.getZoneLogUserName());
		assertEquals("l0g", config.getZoneLogUserPassword());
	}

}
