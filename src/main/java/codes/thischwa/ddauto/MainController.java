package codes.thischwa.ddauto;

import codes.thischwa.ddauto.config.ZoneHostConfig;
import codes.thischwa.ddauto.service.*;
import codes.thischwa.ddauto.util.NetUtil;
import codes.thischwa.ddauto.util.ZoneUtil;
import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController implements MainApiRoutes {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	private final ZoneHostConfig conf;

	private final ZoneSdk sdk;

	private final ZoneUpdateLogger updateLogger;
	
	private final ZoneUpdateLogCache cache;

	public MainController(ZoneHostConfig conf, ZoneSdk sdk, ZoneUpdateLogger updateLogger, ZoneUpdateLogCache cache) {
		this.conf = conf;
		this.sdk = sdk;
		this.updateLogger = updateLogger;
		this.cache = cache;
	}

	@Override
	public ResponseEntity<String> exist(String host) {
		logger.debug("entered #exist: host={}", host);
		if(conf.hostExists(host))
			return ResponseEntity.ok("Host found.");
		return new ResponseEntity<>("Host not found!", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<String> update(String host, String apitoken, String ipv4Str, String ipv6Str, HttpServletRequest req) {
		logger.debug("entered #update: host={}, apitoken={}, ipv4={}, ipv6={}", host, apitoken, ipv4Str, ipv6Str);

		// validation
		if(!conf.hostExists(host))
			return new ResponseEntity<>("Host not found!", HttpStatus.NOT_FOUND);
		String validApitoken = conf.getApitoken(host);
		if(!validApitoken.equals(apitoken))
			return new ResponseEntity<>("Stop processing, unknown 'apitoken': " + apitoken, HttpStatus.BAD_REQUEST);
		if(ipv4Str != null && !NetUtil.isIPv4(ipv4Str))
			return new ResponseEntity<>("Request parameter 'ipv4' isn't valid: " + ipv4Str, HttpStatus.BAD_REQUEST);
		if(ipv6Str != null && !NetUtil.isIPv6(ipv6Str))
			return new ResponseEntity<>("Request parameter 'ipv6' isn't valid: " + ipv6Str, HttpStatus.BAD_REQUEST);
		if(ipv4Str == null && ipv6Str == null) {
			logger.debug("Both IP parameters are null, try to fetch the remote IP.");
			String remoteIP = req.getRemoteAddr();
			if(remoteIP == null) {
				logger.error("Couldn't determine the remote ip!");
				return new ResponseEntity<>("Couldn't determine the remote ip!", HttpStatus.BAD_REQUEST);
			}
			logger.debug("Fetched remote IP: {}", remoteIP);
			if(NetUtil.isIPv6(remoteIP))
				ipv6Str = remoteIP;
			else
				ipv4Str = remoteIP;
		}

		// processing the update
		try {
			boolean processed = sdk.zoneUpdate(host, ipv4Str, ipv6Str);
			if(processed) {
				logger.info("Updated host {} successful with ipv4={}, ipv6={}", host, ipv4Str, ipv6Str);
				updateLogger.log(host, ipv4Str, ipv6Str);
				return ResponseEntity.ok("Update successful.");
			} else {
				logger.debug("IPs hasn't changed for {}, no update required!", host);
				return ResponseEntity.ok("Update not required. IPs haven't changed.");
			}
		} catch (ZoneSdkException e) {
			logger.error("Updated host failed: " + host, e);
			return new ResponseEntity<>("Update failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> info(String host) {
		logger.debug("entered #info: host={}", host);
		if(!conf.hostExists(host))
			return new ResponseEntity<>("Host not found.", HttpStatus.NOT_FOUND);

		Zone zone;
		try {
			zone = sdk.zoneInfo(host);
		} catch (ZoneSdkException e) {
			logger.error("Zone info failed for: " + host, e);
			return new ResponseEntity<>("Zone info failed.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String sld = host.substring(0, host.indexOf("."));
		ResourceRecord rr = ZoneUtil.searchResourceRecord(zone, sld, ZoneUtil.ResouceRecordTypeIP.A);
		String ipv4Str = (rr == null) ? "n/a" : rr.getValue();
		rr = ZoneUtil.searchResourceRecord(zone, sld, ZoneUtil.ResouceRecordTypeIP.AAAA);
		String ipv6Str = (rr == null) ? "n/a" : rr.getValue();
		StringBuilder info = new StringBuilder();
		info.append("IP settings for host: ").append(host).append('\n');
		info.append("IPv4: ").append(ipv4Str).append('\n');
		info.append("IPv6: ").append(ipv6Str).append('\n');
		return ResponseEntity.ok(info.toString());
	}

	@Override
	public ResponseEntity<String> getMemoryStatistics() {
		StringBuilder memInfo = new StringBuilder();
		memInfo.append("Basic memory Information:\n");
		long mb = 1024L * 1024L;
		memInfo.append(String.format("Total: %6d MB\n", Runtime.getRuntime().totalMemory() / mb));
		memInfo.append(String.format("Max:   %6d MB\n", Runtime.getRuntime().maxMemory() / mb));
		memInfo.append(String.format("Free:  %6d MB\n", Runtime.getRuntime().freeMemory() / mb));
		return ResponseEntity.ok(memInfo.toString());
	}
	
	@Override
	public ResponseEntity<ZoneLogPage> getZoneUpdateLogs(@RequestParam(required = false) Integer page, @RequestParam(required = false) String search) {
		return (page == null && search == null) ? ResponseEntity.ok(cache.getResponseAll()) : ResponseEntity.ok(cache.getResponsePage(page, search));
	}
	
}
