package codes.thischwa.ddauto.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import codes.thischwa.ddauto.DDAutoStarter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { DDAutoStarter.class })
@ExtendWith(SpringExtension.class)
class ZoneUpdateLogCachePaginationTest {

	@Autowired
	private ZoneUpdateLogCache cache;
	
	@Test 
	final void testPageFirst() {
		ZoneLogPage lp = cache.getResponsePage(1);
		assertEquals(1, lp.getPage());
		assertEquals(4, lp.getPageSize());
		assertEquals(9, lp.getTotalPage());
		assertEquals(38, lp.getTotal());
		assertEquals(4, lp.getItems().size());
		assertNull(lp.getQueryStringPrev());
		assertEquals("?p=2", lp.getQueryStringNext());
		assertEquals("2022-02-01 03:28:11.497", lp.getItems().get(0).getDateTime());
		assertEquals("2022-02-02 03:32:27.796", lp.getItems().get(3).getDateTime());
	}

	@Test
	final void testPageSecond() {
		ZoneLogPage lp = cache.getResponsePage(2);
		assertEquals(2, lp.getPage());
		assertEquals(4, lp.getPageSize());
		assertEquals(9, lp.getTotalPage());
		assertEquals(38, lp.getTotal());
		assertEquals(4, lp.getItems().size());
		assertEquals("?p=1", lp.getQueryStringPrev());
		assertEquals("?p=3", lp.getQueryStringNext());
		assertEquals("2022-02-03 03:25:34.745", lp.getItems().get(0).getDateTime());
		assertEquals("2022-02-04 03:30:19.210", lp.getItems().get(3).getDateTime());
	}

	@Test
	final void testPageLast() {
		ZoneLogPage lp = cache.getResponsePage(9);
		assertEquals(9, lp.getPage());
		assertEquals(4, lp.getPageSize());
		assertEquals(9, lp.getTotalPage());
		assertEquals(38, lp.getTotal());
		assertEquals(2, lp.getItems().size());
		assertEquals("?p=8", lp.getQueryStringPrev());
		assertNull(lp.getQueryStringNext());
		assertEquals("2022-02-17 03:08:17.401", lp.getItems().get(0).getDateTime());
		assertEquals("2022-02-17 03:39:37.606", lp.getItems().get(1).getDateTime());
	}
}
