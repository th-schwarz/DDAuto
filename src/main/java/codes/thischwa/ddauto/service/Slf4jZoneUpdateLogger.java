package codes.thischwa.ddauto.service;

import codes.thischwa.ddauto.config.ZoneHostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Optional;

/**
 * Implementation of {@link ZoneUpdateLogger} that implies an extra log configuration for "ZoneUpdateLogger"
 * witch logs into an extra file.
 */
@Service
public class Slf4jZoneUpdateLogger implements ZoneUpdateLogger, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger("ZoneUpdateLogger");

	private final ZoneHostConfig conf;
	
	private final ZoneUpdateLogCache cache;

	private String logEntryFormat;

	public Slf4jZoneUpdateLogger(ZoneHostConfig conf, ZoneUpdateLogCache cache) {
		this.conf = conf;
		this.cache = cache;
	}

	@Override
	public void log(String host, String ipv4, String ipv6) {
		Assert.notNull(host, "'host' shouldn't be null.");
		logger.info(buildLogEntry(logEntryFormat, host, ipv4, ipv6));
		cache.addLogEntry(host, ipv4, ipv6);
	}
	
	// it's static, just for testing
	static String buildLogEntry(String logEntryFormat, String host, String ipv4, String ipv6) {
		ipv4 = (ipv4 == null) ? "n/a" : ipv4;
		ipv6 = (ipv6 == null) ? "n/a" : ipv6;
		return String.format(logEntryFormat, host, ipv4, ipv6);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// determine the max. length of the hosts for nicer logging
		int maxSize = conf.getConfiguredHosts().stream()
				.max(Comparator.comparing(String::length)).map(max -> max.length()).orElse(12);
		logEntryFormat = "%" + maxSize + "s  %16s  %s";
	}
}
