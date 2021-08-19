package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class })
@ExtendWith(SpringExtension.class)
class DDAutoConfigTest {

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
}
