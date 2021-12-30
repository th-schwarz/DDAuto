package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class })
@ExtendWith(SpringExtension.class)
class DDAutoConfigTest {

	private final int configuredEntries = 2;
	
	@BeforeEach
	void setUp() {
		config.readData();
	}

	@Autowired
	private DDAutoConfig config;
	
	@Test
	final void testCountZones() {
		assertEquals(2, config.getZones().size());
	}

	@Test
	final void testZoneDetails() {
		DDAutoConfig.Zone zone = config.getZones().get(0);
		assertEquals("dynhost0.info", zone.getName());
		assertEquals("ns0.domain.info", zone.getNs());
		
		assertEquals("my0:1234567890abcdef", zone.getHosts().get(0));
		assertEquals("test1:1234567890abcdx", zone.getHosts().get(1));
	}

	@Test
	final void testGetApiToken() {
		assertEquals("1234567890abcdef", config.getApitoken("my0.dynhost0.info"));
		assertThrows(IllegalArgumentException.class, () -> {
			config.getApitoken("unknown.host.info");
		});
	}

	@Test
	final void testgetPrimaryNameServer() {
		assertEquals("ns1.domain.info", config.getPrimaryNameServer("dynhost1.info"));
		assertThrows(IllegalArgumentException.class, () -> {
			config.getPrimaryNameServer("unknown-host.info");
		});
	}

	@Test
	final void testConfigured() {
		assertEquals(configuredEntries*2, config.getConfiguredHosts().size());
		assertEquals(configuredEntries, config.getConfiguredZones().size());
	}
	
	@Test
	final void testValidateData_ok() {
		config.validateData();
	}

}
