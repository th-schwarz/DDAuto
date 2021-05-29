package codes.thischwa.autodyn.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.domainrobot.sdk.client.JsonUtils;
import org.domainrobot.sdk.models.generated.JsonResponseDataZone;
import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
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
	}
}
