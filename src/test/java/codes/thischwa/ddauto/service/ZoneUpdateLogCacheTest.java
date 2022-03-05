package codes.thischwa.ddauto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import codes.thischwa.ddauto.DDAutoStarter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class })
@ExtendWith(SpringExtension.class)
class ZoneUpdateLogCacheTest {

	private final int startCnt = 4;

	@Autowired
	private ZoneUpdateLogCache cache;

	@Test
	final void testLength() {
		assertEquals(startCnt, cache.length());
		assertEquals(startCnt, cache.get().size());
	}

	@Test
	final void testAddLogEntry() {
		assertEquals(startCnt, cache.length());
		cache.addLogEntry("my.dyndns.com", "91.0.0.1", null);
		assertEquals(startCnt + 1, cache.length());

		ZoneUpdateItem item = cache.get().remove(startCnt);
		assertEquals("my.dyndns.com", item.getHost());
		assertEquals("91.0.0.1", item.getIpv4());
		assertEquals("n/a", item.getIpv6());

		cache.addLogEntry("my.dyndns.com", "91.0.0.1", "2003:cc:2fff:1131:2e91:abff:febf:d839");
		item = cache.get().remove(startCnt);
		assertEquals("my.dyndns.com", item.getHost());
		assertEquals("91.0.0.1", item.getIpv4());
		assertEquals("2003:cc:2fff:1131:2e91:abff:febf:d839", item.getIpv6());
	}

	@Test
	final void testItem() {
		assertEquals("ZoneUpdateItem [dateTime=2022-02-01 03:28:11.497, host=ursa.mydyndns.com, ipv4=217.229.130.11, ipv6=n/a]",
				cache.get().get(0).toString());
	}

}
