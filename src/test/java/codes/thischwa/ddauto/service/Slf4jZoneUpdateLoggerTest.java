package codes.thischwa.ddauto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Slf4jZoneUpdateLoggerTest {
	
	@Test
	final void testBuildLogEntry() {
		assertEquals("mydyndns.domain.org         127.1.2.4  2a03:4000:41:32::3", 
				Slf4jZoneUpdateLogger.buildLogEntry("%s  %16s  %s" ,"mydyndns.domain.org", "127.1.2.4", "2a03:4000:41:32::3"));
		assertEquals("mydyndns.domain.org         127.1.2.4  n/a", 
				Slf4jZoneUpdateLogger.buildLogEntry("%s  %16s  %s" ,"mydyndns.domain.org", "127.1.2.4", null));
		assertEquals("mydyndns.domain.org               n/a  2a03:4000:41:32::3", 
				Slf4jZoneUpdateLogger.buildLogEntry("%s  %16s  %s" ,"mydyndns.domain.org", null, "2a03:4000:41:32::3"));
	}

}
