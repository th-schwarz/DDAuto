package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.DDAutoStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class, DDAutoConfig.class })
class DDAutoConfigTest {

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
