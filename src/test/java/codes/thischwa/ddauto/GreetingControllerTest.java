package codes.thischwa.ddauto;

import codes.thischwa.ddauto.util.NetUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreetingControllerTest extends GenericIntegrationTest {

	@Test
	void greetingShouldReturnDefaultMessage() {
		assertTrue(this.restTemplate.getForObject("http://localhost:" + port, String.class).contains("DDAuto"));
	}

	@Test
	void baseUrlTestP() {
		// feed the mock
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setContextPath("/");
		mockRequest.setServerPort(port);
		ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
		RequestContextHolder.setRequestAttributes(attrs);

		assertEquals("http://localhost:" + port, NetUtil.getBaseUrl(false));
		assertEquals("https://localhost:" + port, NetUtil.getBaseUrl(true));
	}
}
