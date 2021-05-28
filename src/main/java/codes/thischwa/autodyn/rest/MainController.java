package codes.thischwa.autodyn.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	private Context context;

	@RequestMapping(value = "/exist/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> exist(@PathVariable String host) {
		logger.debug("entered #exist: host={}", host);
		if(context.hostExists(host)) 
			return ResponseEntity.ok("Host found.");
		return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
	}
	

	@RequestMapping(value = "/update/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> update(@PathVariable String host, @RequestParam(name = "ipv4", required = false) String ipv4Str,  @RequestParam(name = "ipv6", required = false) String ipv6Str) {
		logger.debug("entered #update: host={}, ipv4={}, ipv6={}", host, ipv4Str, ipv6Str);
		
		// validation
		if(!context.hostExists(host)) 
			return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
		if(ipv4Str == null && ipv6Str == null)
			return new ResponseEntity<String>("At least one of the following request parameter must be set: ipv4, ipv6", 
					HttpStatus.BAD_REQUEST);
		if(ipv4Str != null && !validateIP(ipv4Str))
			return new ResponseEntity<String>("Request parameter 'ipv4' isn't valid: " + ipv4Str, 
					HttpStatus.BAD_REQUEST);
		if(ipv6Str != null && !validateIP(ipv6Str))
			return new ResponseEntity<String>("Request parameter 'ipv6' isn't valid: " + ipv6Str, 
					HttpStatus.BAD_REQUEST);
		
		// processing the update
		
		return ResponseEntity.ok("Update successful.");
	}
	
	boolean validateIP(String ipStr) {
		try {
			InetAddress.getByAddress(ipStr.getBytes());
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}
}
