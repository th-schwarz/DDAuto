package codes.thischwa.ddauto.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

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

	private final Pattern logEntryPattern = Pattern.compile("(.*)\\s+-\\s+([a-zA-Z\\.-]*)\\s+(\\S*)\\s+(\\S*)");

	@Autowired
	private ZoneUpdateLogCache cache;

	@Test
	final void testCacheSize() {
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

	@Test
	final void testParseLogEntry() {
		assertNull(cache.parseLogEntry(null, logEntryPattern));
		assertNull(cache.parseLogEntry("abc", logEntryPattern));

		ZoneUpdateItem item = cache.parseLogEntry(
				"2022-02-23 19:51:19.924 -   test.mein-virtuelles-blech.de        127.1.2.27  2a03:4000:41:32::2", logEntryPattern);
		assertEquals("2022-02-23 19:51:19.924", item.getDateTime());
		assertEquals("test.mein-virtuelles-blech.de", item.getHost());
		assertEquals("127.1.2.27", item.getIpv4());
		assertEquals("2a03:4000:41:32::2", item.getIpv6());
	}

	@Test
	final void getResponseAll() {
		LogWrapper lw = cache.getResponseAll();
		assertEquals(startCnt, lw.getTotal());
		assertEquals(startCnt, lw.getItems().size());
		assertEquals(0, lw.getTotalPage());
		assertEquals(0, lw.getPage());
	}
}
