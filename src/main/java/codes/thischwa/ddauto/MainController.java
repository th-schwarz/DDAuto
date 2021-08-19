package codes.thischwa.ddauto;

import javax.servlet.http.HttpServletRequest;

import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codes.thischwa.ddauto.service.UpdateLogger;
import codes.thischwa.ddauto.service.UpdateLoggerException;
import codes.thischwa.ddauto.service.ZoneSdk;
import codes.thischwa.ddauto.service.ZoneSdkException;
import codes.thischwa.ddauto.util.ZoneUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "main", description = "The routes.")
@RestController
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private DDAutoContext context;

	@Autowired
	private ZoneSdk sdk;

	@Autowired
	private UpdateLogger updateLogger;

	@Operation(summary = "Checks, if the 'host' exists and is configured.", tags = "host")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Host exists and is configured.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host found.") })),
			@ApiResponse(responseCode = "404", description = "Host doesn't exists.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host not found.") })) })
	@GetMapping(value = "/exist/{host}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> exist(
			@Parameter(description = "The desired host to check.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host) {
		logger.debug("entered #exist: host={}", host);
		if(context.hostExists(host))
			return ResponseEntity.ok("Host found.");
		return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Updates the desired IP addresses of the 'host', if the 'apitoken' belongs to the host. If both parameters for IP addresses aren't set, an attempt is made to fetch the remote IP.", tags = "host")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updates the desired IP addresses of the 'host', if the 'apitoken' belongs to the host. If both parameters for IP addresses aren't set, an attempt is made to fetch the remote IP.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Update successful.") })),
			@ApiResponse(responseCode = "400", description = "If the 'apitoken' doesn't match the 'host', IP addresses aren't valid or the remote IP couldn't determine."),
			@ApiResponse(responseCode = "500", description = "If the zone update fails.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Zone update failed.") })) })
	@GetMapping(value = "/update/{host}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> update(
			@Parameter(description = "The host, for which the IPs must be updated.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host, @RequestParam String apitoken, @RequestParam(name = "ipv4", required = false) String ipv4Str,
			@RequestParam(name = "ipv6", required = false) String ipv6Str, HttpServletRequest req) {
		logger.debug("entered #update: host={}, apitoken={}, ipv4={}, ipv6={}", host, apitoken, ipv4Str, ipv6Str);

		// validation
		if(!context.hostExists(host))
			return new ResponseEntity<String>("Host not found!", HttpStatus.NOT_FOUND);
		String validApitoken = context.getApitoken(host);
		if(!validApitoken.equals(apitoken))
			return new ResponseEntity<String>("Stop processing, unknown 'apitoken': " + apitoken, HttpStatus.BAD_REQUEST);
		if(ipv4Str != null && !ZoneUtil.isValidateIP(ipv4Str))
			return new ResponseEntity<String>("Request parameter 'ipv4' isn't valid: " + ipv4Str, HttpStatus.BAD_REQUEST);
		if(ipv6Str != null && !ZoneUtil.isValidateIP(ipv6Str))
			return new ResponseEntity<String>("Request parameter 'ipv6' isn't valid: " + ipv6Str, HttpStatus.BAD_REQUEST);
		if(ipv4Str == null && ipv6Str == null) {
			logger.debug("Both IP parameters are null, try to fetch the remote IP.");
			String remoteIP = req.getRemoteAddr();
			if(remoteIP == null) {
				logger.error("Couldn't determine the remote ip!");
				return new ResponseEntity<String>("Couldn't determine the remote ip!", HttpStatus.BAD_REQUEST);
			}
			logger.debug("Fetched remote IP: " + remoteIP);
			if(ZoneUtil.isIPv6(remoteIP))
				ipv6Str = remoteIP;
			else
				ipv4Str = remoteIP;
		}

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

	@Operation(summary = "Determine the IP settings of the 'host' and returns it as formatted plain text.", tags = "host")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Formatted plain text with the IP settings of the 'host'"),
			@ApiResponse(responseCode = "404", description = "If the 'host' isn't configured.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host not found.") })),
			@ApiResponse(responseCode = "500", description = "If the zone info fails.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Zone info failed.") })) })
	@GetMapping(value = "/info/{host}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> info(
			@Parameter(description = "The host, for which the IPs must be identified.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host) {
		logger.debug("entered #info: host={}", host);
		if(!context.hostExists(host))
			return new ResponseEntity<String>("Host not found.", HttpStatus.NOT_FOUND);

		Zone zone = null;
		try {
			zone = sdk.zoneInfo(host);
		} catch (ZoneSdkException e) {
			logger.error("Zone info failed for: " + host, e);
			return new ResponseEntity<String>("Zone info failed.", HttpStatus.INTERNAL_SERVER_ERROR);
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

	@Operation(summary = "Generates basic memory informations", tags = "info")
	@ApiResponse(responseCode = "200", description = "Textual information about the memory.")
	@GetMapping(value = "meminfo", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getMemoryStatistics() {
		StringBuilder memInfo = new StringBuilder();
		memInfo.append("Basic memory Information:\n");
		memInfo.append(String.format("Total: %6d MB\n", Runtime.getRuntime().totalMemory() / (1024l * 1024l)));
		memInfo.append(String.format("Max:   %6d MB\n", Runtime.getRuntime().maxMemory() / (1024l * 1024l)));
		memInfo.append(String.format("Free:  %6d MB\n", Runtime.getRuntime().freeMemory() / (1024l * 1024l)));
		return ResponseEntity.ok(memInfo.toString());
	}
}
