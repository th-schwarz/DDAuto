package codes.thischwa.ddauto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A simple controller that delivers a welcome page without basic-auth.
 */
@Controller
@ConditionalOnProperty(name = "greeting.enabled", matchIfMissing = true)
public class GreetingContoller {

	/**
	 * Delivers a welcome page.
	 * 
	 * @return the welcome page
	 */
	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String greeting() {
		return "about.html";
	}
}
