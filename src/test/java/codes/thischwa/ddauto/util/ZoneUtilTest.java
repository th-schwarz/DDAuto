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

import java.util.Objects;

class ZoneUtilTest {

	private Zone zone;
	
	private static final int rrCount = 5;

	@BeforeEach
	void setUp() throws Exception {
		JsonResponseDataZone response = JsonUtils.deserialize(Objects.requireNonNull(this.getClass().getResourceAsStream("zone-info.json")).readAllBytes(),
				JsonResponseDataZone.class);
		zone = response.getData().get(0);
	}

	@Test
	final void testUpdateIPv4() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub", "128.0.0.1");
		assertEquals(rrCount, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub", ZoneUtil.ResouceRecordTypeIP.A);
		assertNotNull(rr);
		assertEquals("128.0.0.1", rr.getValue());
	}

	@Test
	final void testUpdateIPv6() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv6(zone, "sub", "2a03:4000:41:32::20");
		assertEquals(rrCount, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub", ZoneUtil.ResouceRecordTypeIP.AAAA);
		assertNotNull(rr);
		assertEquals("2a03:4000:41:32::20", rr.getValue());
	}

	@Test
	final void testAddIPv4() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub1", "128.0.0.1");
		assertEquals(rrCount+1, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub1", ZoneUtil.ResouceRecordTypeIP.A);
		assertNotNull(rr);
		assertEquals("128.0.0.1", rr.getValue());
	}

	@Test
	final void testAddIPv6() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv6(zone, "sub1", "2a03:4000:41:32::20");
		assertEquals(rrCount+1, zone.getResourceRecords().size());
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, "sub1", ZoneUtil.ResouceRecordTypeIP.AAAA);
		assertNotNull(rr);
		assertEquals("2a03:4000:41:32::20", rr.getValue());
	}
	
	@Test
	final void testRemoveIPv4() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv4(zone, "sub2", "128.0.0.2");
		assertEquals(rrCount+1, zone.getResourceRecords().size());
		ZoneUtil.removeIPv4(zone, "sub2");
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.removeIPv4(zone, "unknownsub");
		assertEquals(rrCount, zone.getResourceRecords().size());
	}

	@Test
	final void testRemoveIPv6() {
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.addOrUpdateIPv6(zone, "sub2", "2a03:4000:41:32::20");
		assertEquals(rrCount+1, zone.getResourceRecords().size());
		ZoneUtil.removeIPv6(zone, "sub2");
		assertEquals(rrCount, zone.getResourceRecords().size());
		ZoneUtil.removeIPv6(zone, "unknownsub");
		assertEquals(rrCount, zone.getResourceRecords().size());
	}

	@Test
	final void testDeriveZone() {
		assertEquals("example.com", ZoneUtil.deriveZone("sub.example.com"));
	}
	
	@Test
	final void testDeriveZone_fail() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> ZoneUtil.deriveZone("example.com"));
	}
	
	@Test
	final void testIPsHasChanged() {
		assertFalse(ZoneUtil.hasIPsChanged(zone, "sub", "85.209.51.215", "2a03:4000:41:32::10"));		
		assertTrue(ZoneUtil.hasIPsChanged(zone, "unknownsub", "85.209.51.216", "2a03:4000:41:32::10"));

		assertTrue(ZoneUtil.hasIPsChanged(zone, "sub", "85.209.51.216", "2a03:4000:41:32::10"));
		assertTrue(ZoneUtil.hasIPsChanged(zone, "sub", "85.209.51.215", "2a03:4000:41:32::11"));

		assertTrue(ZoneUtil.hasIPsChanged(zone, "sub", null, "2a03:4000:41:32::10"));
		assertTrue(ZoneUtil.hasIPsChanged(zone, "sub", "85.209.51.215", null));
	}
}
