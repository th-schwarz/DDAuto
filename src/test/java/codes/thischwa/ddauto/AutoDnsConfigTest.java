package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { AutoDnsConfig.class })
@ExtendWith(SpringExtension.class)
class AutoDnsConfigTest {
	
	@Autowired
	private AutoDnsConfig config;

	@Test
	final void testGetUrl() {
		assertEquals("https://api.autodns.com/v1", config.getUrl());
	}

	@Test
	final void testGetContext() {
		assertEquals(4, config.getContext());
	}

	@Test
	final void testGetUser() {
		assertEquals("user", config.getUser());
	}

	@Test
	final void testGetPassword() {
		assertEquals("pwd", config.getPassword());
	}

}
