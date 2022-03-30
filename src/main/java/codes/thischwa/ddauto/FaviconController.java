package codes.thischwa.ddauto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;

@Controller
public class FaviconController {

	@Operation(hidden = true)
    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    	// just for reducing 404 errors in the logs
    }
}
