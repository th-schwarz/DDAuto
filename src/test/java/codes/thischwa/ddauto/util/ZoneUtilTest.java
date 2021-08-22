package codes.thischwa.ddauto.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.domainrobot.sdk.client.JsonUtils;
import org.domainrobot.sdk.models.generated.JsonResponseDataZone;
import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoneUtilTest {

	private Zone zone;

	@BeforeEach
	void setUp() throws Exception {
		JsonResponseDataZone response = JsonUtils.deserialize(this.getClass().getResourceAsStream("zone-info.json").readAllBytes(),
				JsonResponseDataZone.class);
		zone = response.getData().get(0);
	}

	@Test
	final void testUpdateIPv4() {
		assertEquals(4, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub", "128.0.0.1");
		assertEquals(4, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub", "A");
		assertNotNull(rr);
		assertEquals("128.0.0.1", rr.getValue());
	}

	@Test
	final void testAddIPv4() {
		assertEquals(4, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub1", "128.0.0.1");
		assertEquals(5, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub1", "A");
		assertNotNull(rr);
		assertEquals("128.0.0.1", rr.getValue());
	}

	@Test
	final void testRemoveIPv4() {
		assertEquals(4, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub2", "128.0.0.2");
		assertEquals(5, zone.getResourceRecords().size());
		ZoneUtil.removeIPv4(zone, "sub2");
		assertEquals(4, zone.getResourceRecords().size());
		ZoneUtil.removeIPv4(zone, "unknownsub");
		assertEquals(4, zone.getResourceRecords().size());
	}
	
	@Test
	final void testValidIP() {
		assertTrue(ZoneUtil.isValidateIP("217.229.139.240"));
		assertTrue(ZoneUtil.isValidateIP("2a03:4000:41:32::1"));

		assertFalse(ZoneUtil.isValidateIP("300.229.139.240"));
		assertFalse(ZoneUtil.isValidateIP("2x03:4000:41:32::1"));
	}

	@Test
	final void testDeriveZone() {
		assertEquals("example.com", ZoneUtil.deriveZone("sub.example.com"));
	}
	
	@Test
	final void testDeriveZone_fail() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ZoneUtil.deriveZone("example.com");
		});
	}
	
	@Test
	final void testIfIPv6() {
		assertTrue(ZoneUtil.isIPv6("2a03:4000:41:32::1"));
		assertFalse(ZoneUtil.isIPv6("217.229.139.240"));
	}

	@Test
	final void testIfIPv6_fail1() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ZoneUtil.isIPv6("2a03.4000:41:32::1");
		});
	}
	
	@Test
	final void testIfIPv6_fail() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			ZoneUtil.isIPv6("217:229.139.240");
		});
	}
}
