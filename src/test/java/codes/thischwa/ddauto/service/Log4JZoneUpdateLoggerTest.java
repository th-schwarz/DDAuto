package codes.thischwa.ddauto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Log4JZoneUpdateLoggerTest {
	
	@Test
	final void testBuildLogEntry() throws Exception {
		assertEquals("mydyndns.domain.org         127.1.2.4  2a03:4000:41:32::3", 
				Log4JZoneUpdateLogger.buildLogEntry("%s  %16s  %s" ,"mydyndns.domain.org", "127.1.2.4", "2a03:4000:41:32::3"));
	}

}
