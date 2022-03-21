package codes.thischwa.ddauto;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import codes.thischwa.ddauto.util.NetUtil;

/**
 * A controller that delivers a page to show the zone update logs.
 */
@Controller
@ConditionalOnProperty(name = "ddauto.zone-log-page-enabled", matchIfMissing = true)
public class ZoneUpdateLogContoller {
	
	@Value("${spring.security.user.name}")
	private String user;
	
	@Value("${spring.security.user.password}")
	private String password;
	
	/**
	 * Delivers page to show the zone update logs.
	 * 
	 * @return the zone update logs page
	 */
	@GetMapping(value = "/log", produces = MediaType.TEXT_HTML_VALUE)
	public String log(Model model, HttpServletRequest request) {
		String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
		        .replacePath(null)
		        .build()
		        .toUriString();

		model.addAttribute("server_url", baseUrl + "/info/zone-log");
		if(user != null && password != null) {
			String basicAuth = NetUtil.buildBasicAuth(user, password);
			model.addAttribute("header_basicauth", basicAuth);
		}
		return "log-zone.html";
	}
}
