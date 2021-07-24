package codes.thischwa.ddauto;

import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import codes.thischwa.ddauto.service.UpdateLogger;
import codes.thischwa.ddauto.service.UpdateLoggerException;
import codes.thischwa.ddauto.service.ZoneSdk;
import codes.thischwa.ddauto.service.ZoneSdkException;
import codes.thischwa.ddauto.util.ZoneUtil;

@Controller
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private DDAutoContext context;

	@Autowired
	private ZoneSdk sdk;

	@Autowired
	private UpdateLogger updateLogger;

	/**
	 * Checks, if the 'host' is configured / exists.
	 * 
	 * @param host
	 *            The desired host to check.
	 * @return If the host is configured, the plain text 'Host found.' will be returned. Otherwise 'Host not found!' and http status 404
	 *         (not found)
	 */
	@RequestMapping(value = "/exist/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> exist(@PathVariable String host) {
		logger.debug("entered #exist: host={}", host);
		if(context.hostExists(host))
			return ResponseEntity.ok("Host found.");
		return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
	}

	/**
	 * Updates the desired IP addresses of the 'host', if the 'apitoken' belongs to the host.
	 * 
	 * @param host
	 *            The host, for which the IPs must be updated.
	 * @param apitoken
	 *            The api-token.
	 * @param ipv4Str
	 *            The IPv4 address.
	 * @param ipv6Str
	 *            The IPv6 address, optional.
	 * @return If the 'host' isn't configured http status 404 (not found). <br>
	 *         If the 'apitoken' doesn't match the 'host' or IP addresses aren't valid http status 400 (bad request). If the zone-update
	 *         fails http status 500 (internal server error), <br>
	 *         or the plain text 'Update successful.'
	 */
	@RequestMapping(value = "/update/{host}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> update(@PathVariable String host, @RequestParam String apitoken,
			@RequestParam(name = "ipv4", required = false) String ipv4Str, @RequestParam(name = "ipv6", required = false) String ipv6Str) {
		logger.debug("entered #update: host={}, apitoken={}, ipv4={}, ipv6={}", host, apitoken, ipv4Str, ipv6Str);

		// validation
		if(!context.hostExists(host))
			return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
		String validApitoken = context.getApitoken(host);
		if(!validApitoken.equals(apitoken))
			return new ResponseEntity<String>("Stop processing, unknown 'apitoken': " + apitoken, HttpStatus.BAD_REQUEST);
		if(ipv4Str == null && ipv6Str == null)
			return new ResponseEntity<String>("At least one of the following request parameter must be set: ipv4, ipv6",
					HttpStatus.BAD_REQUEST);
		if(ipv4Str != null && !ZoneUtil.validateIP(ipv4Str))
			return new ResponseEntity<String>("Request parameter 'ipv4' isn't valid: " + ipv4Str, HttpStatus.BAD_REQUEST);
		if(ipv6Str != null && !ZoneUtil.validateIP(ipv6Str))
			return new ResponseEntity<String>("Request parameter 'ipv6' isn't valid: " + ipv6Str, HttpStatus.BAD_REQUEST);

		// processing the update
		try {
			sdk.zoneUpdate(host, ipv4Str, ipv6Str);
			logger.info("Updated host {} successful with ipv4={}, ipv6={}", host, ipv4Str, ipv6Str);
			updateLogger.log(host, ipv4Str, ipv6Str);
		} catch (ZoneSdkException e) {
			logger.error("Updated host failed: " + host, e);
			return new ResponseEntity<String>("Update failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UpdateLoggerException e) {
			logger.error("Error while writing to zone log.", e);
		}
		return ResponseEntity.ok("Update successful.");
	}

	/**
	 * Determine the IP settings of the 'host' and returns it as formatted plain text.
	 * 
	 * @param host
	 *            The host, for which the IPs must be identified.
	 * @return If the 'host' is configured and the zone info was successful, a plain text will be, which contains the IPs. <br>
	 *         If the 'host' isn't configured http status 404 (not found). <br>
	 *         If the zone-info fails, http status 500 (internal server error).
	 */
	@RequestMapping(value = "/info/{host}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> info(@PathVariable String host) {
		logger.debug("entered #info: host={}", host);
		if(!context.hostExists(host))
			return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);

		Zone zone = null;
		try {
			zone = sdk.zoneInfo(host);
		} catch (ZoneSdkException e) {
			logger.error("Zone info failed for: " + host, e);
			return new ResponseEntity<String>("Zone info failed!", HttpStatus.INTERNAL_SERVER_ERROR);
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

	@GetMapping("meminfo")
	public ResponseEntity<String> getMemoryStatistics() {
		StringBuilder memInfo = new StringBuilder();
		memInfo.append("Basic memory Information:\n");
		memInfo.append(String.format("Total: %6d MB\n", Runtime.getRuntime().totalMemory() / (1024l * 1024l)));
		memInfo.append(String.format("Max:   %6d MB\n", Runtime.getRuntime().maxMemory() / (1024l * 1024l)));
		memInfo.append(String.format("Free:  %6d MB\n", Runtime.getRuntime().freeMemory() / (1024l * 1024l)));
		return ResponseEntity.ok(memInfo.toString());
	}
}
