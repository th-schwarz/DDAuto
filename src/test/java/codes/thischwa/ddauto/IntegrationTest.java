package codes.thischwa.ddauto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import codes.thischwa.ddauto.util.NetUtil;

@AutoConfigureMockMvc
class IntegrationTest extends GenericIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;
    
    @Test
    final void testNoAuth_Greeting() throws Exception {
        mockMvc.perform(get("/"))
        	//.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType("text", "html", StandardCharsets.UTF_8)))
            .andExpect(content().string(containsString("DDAuto :: Default landing page")));
    }
    
	@Test
	final void testBasicAuth_log() throws Exception {
		mockMvc.perform(get("/log").with(httpBasic("log", "l0g")))
//			.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType("text", "html", StandardCharsets.UTF_8)))
            .andExpect(content().string(containsString("DDAuto :: Log View")));
	}

	@Test
	final void testBasicAuth_global() throws Exception {
		mockMvc.perform(get("/info/zone-log").queryParam("page", "0").with(httpBasic("dyndns", "test123")))
			.andDo(print())
	        .andExpect(status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
	        .andExpect(content().string(containsString("{\"total\":38,\"totalPage\":10,\"page\":1,\"pageSize\":4")));
	}

	@Test
	final void baseUrlTestP() {
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
