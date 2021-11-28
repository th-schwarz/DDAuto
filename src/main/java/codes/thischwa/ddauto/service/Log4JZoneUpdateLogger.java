package codes.thischwa.ddauto.service;

import java.util.Comparator;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codes.thischwa.ddauto.DDAutoContext;

@Service
public class Log4JZoneUpdateLogger implements ZoneUpdateLogger {

	private static final Logger logger = LoggerFactory.getLogger(Log4JZoneUpdateLogger.class);

	@Autowired
	private DDAutoContext context;

	private String logEntryFormat;
	
	@Override
	public void log(String host, String ipv4, String ipv6) throws UpdateLoggerException {
		logger.info(Log4JZoneUpdateLogger.buildLogEntry(logEntryFormat, host, ipv4, ipv6));
	}
	
	static String buildLogEntry(String logEntryFormat, String host, String ipv4, String ipv6) throws UpdateLoggerException {
		ipv4 = (ipv4 == null) ? "n/a" : ipv4;
		ipv6 = (ipv6 == null) ? "n/a" : ipv6;
		return String.format(logEntryFormat, host, ipv4, ipv6);
	}
	
	@PostConstruct
	void init() {
		// determine the max. length of the hosts for nicer logging
		Optional<String> max = context.getConfiguredHosts().stream().max(Comparator.comparing(String::length));
		int maxSize = max.isPresent() ? max.get().length() : 12;
		logEntryFormat = "%" + maxSize + "s  %16s  %s";
	}

}
