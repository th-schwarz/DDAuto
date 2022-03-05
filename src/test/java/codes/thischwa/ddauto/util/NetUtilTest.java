package codes.thischwa.ddauto.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NetUtilTest {

	@Test
	final void testIPv4() {
		assertTrue(NetUtil.isIPv4("217.229.139.240"));
		assertFalse(NetUtil.isIPv4("300.229.139.240"));
	}
	
	@Test
	final void testIPv6() {
		assertTrue(NetUtil.isIPv6("2a03:4000:41:32::1"));
		assertFalse(NetUtil.isIPv6("2a03.4000:41:32::1"));
		assertFalse(NetUtil.isIPv6("217.229.139.240"));
	}
	
	@Test
	final void testBasicAuth() {
		assertEquals("Basic ZHluZG5zOnRlc3QxMjM=", NetUtil.buildBasicAuth("dyndns", "test123"));
	}
}
