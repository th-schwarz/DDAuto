package codes.thischwa.ddauto;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
class GreetingControllerTest extends GenericIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGreeting() throws Exception {
        this.mockMvc.perform(get("/"))
        	//.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType("text", "html", StandardCharsets.UTF_8)))
            .andExpect(content().string(containsString("DDAuto :: Default landing page")));
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
