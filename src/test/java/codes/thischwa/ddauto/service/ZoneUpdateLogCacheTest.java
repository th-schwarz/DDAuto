package codes.thischwa.ddauto.service;

import codes.thischwa.ddauto.DDAutoStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class })
class ZoneUpdateLogCacheTest {

	private final int startCnt = 38;

	private final Pattern logEntryPattern = Pattern.compile("(.*)\\s+-\\s+([a-zA-Z.-]*)\\s+(\\S*)\\s+(\\S*)");

	@Autowired
	private ZoneUpdateLogCache cache;

	@Test
	final void testCache() {
		assertTrue(cache.enabled());
		assertEquals(startCnt, cache.size());
		assertEquals(startCnt, cache.getItems().size());
	}

	@Test
	final void testAddLogEntry() {
		assertEquals(startCnt, cache.size());
		cache.addLogEntry("my.dyndns.com", "91.0.0.1", null);
		assertEquals(startCnt + 1, cache.size());

		ZoneLogItem item = cache.getItems().remove(startCnt);
		assertEquals("my.dyndns.com", item.getHost());
		assertEquals("91.0.0.1", item.getIpv4());
		assertEquals("n/a", item.getIpv6());

		cache.addLogEntry("my.dyndns.com", "91.0.0.1", "2003:cc:2fff:1131:2e91:abff:febf:d839");
		item = cache.getItems().remove(startCnt);
		assertEquals("my.dyndns.com", item.getHost());
		assertEquals("91.0.0.1", item.getIpv4());
		assertEquals("2003:cc:2fff:1131:2e91:abff:febf:d839", item.getIpv6());
	}

	@Test
	final void testItem() {
		assertEquals("ZoneLogItem [dateTime=2022-02-01 03:28:11.497, host=ursa.mydyndns.com, ipv4=217.229.130.11, ipv6=n/a]",
				cache.getItems().get(0).toString());
	}

	@Test
	final void testParseLogEntry() {
		assertNull(cache.parseLogEntry(null, logEntryPattern));
		assertNull(cache.parseLogEntry("abc", logEntryPattern));

		ZoneLogItem item = cache.parseLogEntry(
				"2022-02-23 19:51:19.924 -   test.mein-virtuelles-blech.de        127.1.2.27  2a03:4000:41:32::2", logEntryPattern);
		assertEquals("2022-02-23 19:51:19.924", item.getDateTime());
		assertEquals("test.mein-virtuelles-blech.de", item.getHost());
		assertEquals("127.1.2.27", item.getIpv4());
		assertEquals("2a03:4000:41:32::2", item.getIpv6());
	}

	@Test
	final void testResponseAll() {
		ZoneLogPage lp = cache.getResponseAll();
		assertEquals(startCnt, lp.getTotal());
		assertEquals(startCnt, lp.getItems().size());
		assertEquals(0, lp.getTotalPage());
		assertEquals(0, lp.getPage());
		assertEquals(4, lp.getPageSize());
	}
}
