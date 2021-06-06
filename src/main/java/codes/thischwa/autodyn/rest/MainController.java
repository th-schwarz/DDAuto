package codes.thischwa.autodyn.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
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
	
	@Autowired
	private DomainrobotSdk sdk;
	
	@Autowired
	private UpdateLogger updateLogger;
	
	@RequestMapping(value = "/exist/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> exist(@PathVariable String host) {
		logger.debug("entered #exist: host={}", host);
		if(context.hostExists(host)) 
			return ResponseEntity.ok("Host found.");
		return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
	}
	

	@RequestMapping(value = "/update/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> update(@PathVariable String host, @RequestParam String apitoken, @RequestParam(name = "ipv4", required = false) String ipv4Str,
			@RequestParam(name = "ipv6", required = false) String ipv6Str) {
		logger.debug("entered #update: host={}, apitoken={}, ipv4={}, ipv6={}", host, apitoken, ipv4Str, ipv6Str);
		
		// validation
		String validApitoken = context.getAccountData().getProperty(host);
		if(!validApitoken.equals(apitoken))
			return new ResponseEntity<String>("Stop processing, unknown 'apitoken': " + apitoken, 
					HttpStatus.BAD_REQUEST);
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
		try {
			sdk.updateZone(host, ipv4Str, ipv6Str);
			logger.info("Updated host {} successful with ipv4={}, ipv6={}", host, ipv4Str, ipv6Str);
			updateLogger.log(host, ipv4Str, ipv6Str);
		} catch (SdkException e) {
			logger.error("Updated host failed: " + host, e);
			return new ResponseEntity<String>("Update failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UpdateLoggerException e) {
			logger.error("Error while writing to zone log.", e);
		}
		return ResponseEntity.ok("Update successful."); 
	}
	

	@RequestMapping(value = "/info/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> info(@PathVariable String host) {
		logger.debug("entered #info: host={}", host);
		if(!context.hostExists(host)) 
			return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
		
		Zone zone = null;
		try {
			zone = sdk.getZoneOfHost(host);
		} catch (SdkException e) {
			logger.error("Zone info for host failed: " + host, e);
			return new ResponseEntity<String>("Info failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String sld = host.substring(0, host.indexOf("."));
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, sld, "A");
		String ipv4Str = (rr == null) ? "n/a" : rr.getValue();
		rr = ZoneUtil.searchResourceRecord(zone, sld, "AAAA");
		String ipv6Str = (rr == null) ? "n/a" : rr.getValue();
		StringBuilder info = new StringBuilder();
		info.append("IP settings for host: ").append(host).append('\n');
		info.append("IPv4: ").append(ipv4Str).append('\n');
		info.append("IPv6: ").append(ipv6Str).append('\n');
		return ResponseEntity.ok(info.toString());
	}
	
	boolean validateIP(String ipStr) {
		try {
			InetAddress.getByName(ipStr);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}
}
