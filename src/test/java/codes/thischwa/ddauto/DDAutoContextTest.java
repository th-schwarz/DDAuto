package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class, DDAutoContext.class })
@ExtendWith(SpringExtension.class)
class DDAutoContextTest {
	
	private final int configuredEntries = 2;

	@Autowired
	private DDAutoContext context;

	@BeforeEach
	void setUp() {
		context.readData();
	}

	@Test
	final void testGetApiToken() {
		assertEquals("1234567890abcdef", context.getApitoken("my0.dynhost0.info"));
		assertThrows(IllegalArgumentException.class, () -> {
			context.getApitoken("unknown.host.info");
		});
	}

	@Test
	final void testgetPrimaryNameServer() {
		assertEquals("ns1.domain.info", context.getPrimaryNameServer("dynhost1.info"));
		assertThrows(IllegalArgumentException.class, () -> {
			context.getPrimaryNameServer("unknown-host.info");
		});
	}

	@Test
	final void testConfigured() {
		assertEquals(configuredEntries*2, context.getConfiguredHosts().size());
		assertEquals(configuredEntries, context.getConfiguredZones().size());
	}
	
	@Test
	final void testValidateData_ok() {
		context.validateData();
	}

	@Test
	final void testValidateData_fail() {
		context._clearData();
		assertThrows(IllegalArgumentException.class, () -> {
			context.validateData();
		});
	}

}
