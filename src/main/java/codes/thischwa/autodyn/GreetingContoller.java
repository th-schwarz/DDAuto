package codes.thischwa.autodyn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingContoller {

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> greeting() throws IOException {
		try (BufferedReader buffer = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/templates/default.html")))) {
			return ResponseEntity.ok(buffer.lines().collect(Collectors.joining("\n")));
		}
	}
}
