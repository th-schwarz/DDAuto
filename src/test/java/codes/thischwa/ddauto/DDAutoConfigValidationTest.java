package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class })
@ExtendWith(SpringExtension.class)
class DDAutoConfigValidationTest {

	@Autowired
	private DDAutoConfig config;
	private static Validator validator;

	@BeforeAll
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	final void testZones() {
		Set<ConstraintViolation<DDAutoConfig>> violations = validator.validate(config);
		assertTrue(violations.isEmpty());
		assertEquals(2, config.getZones().size());
	}

	@Test
	final void testZone_failName() {
		DDAutoConfig.Zone z = buildZone();
		z.setName(null);
		Set<ConstraintViolation<DDAutoConfig.Zone>> violations = validator.validate(z);
		assertEquals(1, violations.size());
		assertEquals("The name of the zone shouldn't be empty.", violations.iterator().next().getMessage());
	}

	final static DDAutoConfig.Zone buildZone() {
		DDAutoConfig.Zone z = new DDAutoConfig.Zone();
		z.setName("test.dyndns.org");
		z.setNs("ns.dyndns.org");
		z.setHosts(Arrays.asList("test1", "test2"));
		return z;
	}
}