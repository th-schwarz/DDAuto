package codes.thischwa.ddauto.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import codes.thischwa.ddauto.DDAutoContext;

/**
 * A very simple logger for zone updates.
 */
@Service
public class SimpleFileLogger implements ZoneUpdateLogger {

	private static final Logger logger = LoggerFactory.getLogger(SimpleFileLogger.class);

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Value("${file.zone.log:zone.log}")
	private String logFileName;

	@Autowired
	private DDAutoContext context;

	private String logEntryFormat;

	@Override
	public synchronized void log(String host, String ipv4, String ipv6) throws UpdateLoggerException {
		LocalDateTime now = LocalDateTime.now();
		String dateStr = now.format(formatter);
		ipv4 = (ipv4 == null) ? "n/a" : ipv4;
		ipv6 = (ipv6 == null) ? "n/a" : ipv6;
		String line = String.format(logEntryFormat, dateStr, host, ipv4, ipv6);
		Path logPath = Paths.get(logFileName);
		try {
			Files.writeString(logPath, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new UpdateLoggerException("Error while writing to zone log file.", e);
		}
	}

	@PostConstruct
	void init() {
		// determine the max. length of the hosts for nicer logging
		Optional<String> max = context.getConfiguredHosts().stream().max(Comparator.comparing(String::length));
		int maxSize = max.isPresent() ? max.get().length() : 12;
		logEntryFormat = "%s  %" + maxSize + "s  %15s  %40s\n";
		
		// create the log file
		Path logPath = Paths.get(logFileName);
		if(!Files.exists(logPath)) {
			try {
				Files.createFile(logPath);
			} catch (IOException e) {
				throw new SecurityException("Zone log file couldn't be created!", e);
			}
			logger.info("Zone log file didn't exists, successful created!");
		} else {
			logger.debug("Zone log file exists!");
		}
	}
}