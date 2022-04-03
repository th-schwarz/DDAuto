package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.DDAutoStarter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class, ZoneHostConfig.class })
class DDAutoConfigValidationTest {

	@Autowired
	private ZoneHostConfig config;
	private static Validator validator;

	@BeforeAll
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	final void testZones() {
		Set<ConstraintViolation<ZoneHostConfig>> violations = validator.validate(config);
		assertTrue(violations.isEmpty());
		assertEquals(2, config.getZones().size());
	}

	@Test
	final void testZone_failName() {
		ZoneHostConfig.Zone z = buildZone();
		z.setName(null);
		Set<ConstraintViolation<ZoneHostConfig.Zone>> violations = validator.validate(z);
		assertEquals(1, violations.size());
		assertEquals("The name of the zone shouldn't be empty.", violations.iterator().next().getMessage());
	}

	static ZoneHostConfig.Zone buildZone() {
		ZoneHostConfig.Zone z = new ZoneHostConfig.Zone();
		z.setName("test.dyndns.org");
		z.setNs("ns.dyndns.org");
		z.setHosts(Arrays.asList("test1", "test2"));
		return z;
	}
}
