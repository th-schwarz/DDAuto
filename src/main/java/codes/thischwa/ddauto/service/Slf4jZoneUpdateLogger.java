package codes.thischwa.ddauto.service;

import java.util.Comparator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import codes.thischwa.ddauto.config.DDAutoConfig;

@Service
public class Slf4jZoneUpdateLogger implements ZoneUpdateLogger, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger("ZoneUpdateLogger");

	@Autowired
	private DDAutoConfig conf;
	
	@Autowired
	private ZoneUpdateCache cache;

	private String logEntryFormat;
	
	@Override
	public void log(String host, String ipv4, String ipv6) {
		Assert.notNull(host, "'host' shouldn't be null.");
		logger.info(Slf4jZoneUpdateLogger.buildLogEntry(logEntryFormat, host, ipv4, ipv6));
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
		Optional<String> max = conf.getConfiguredHosts().stream().max(Comparator.comparing(String::length));
		int maxSize = max.isPresent() ? max.get().length() : 12;
		logEntryFormat = "%" + maxSize + "s  %16s  %s";
	}
}
