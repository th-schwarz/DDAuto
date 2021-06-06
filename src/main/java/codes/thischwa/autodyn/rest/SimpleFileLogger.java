package codes.thischwa.autodyn.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A very simple logger for zone updates.
 */
@Service
public class SimpleFileLogger implements UpdateLogger {

	private static final Logger logger = LoggerFactory.getLogger(SimpleFileLogger.class);
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private static String fileName = "zone.log";
	
	@Value("${dir.data}")
	private String dataDir;
	
	@PostConstruct
	public void init() {
		Path logPath = Paths.get(dataDir, fileName);
		if(!Files.exists(logPath)) {
			try {
				Files.createFile(logPath);
			} catch (IOException e) {
				throw new RuntimeException("Zone log file couldn't be created!", e); 
			}
			logger.info("Zone log file didn't exists, successful created!");
		} else {
			logger.debug("Zone log file exists!");
		}
	}
	
	@Override
	public synchronized void log(String host, String ipv4, String ipv6) throws UpdateLoggerException {
		LocalDateTime now = LocalDateTime.now();
		String dateStr = now.format(formatter);
		ipv4 = (ipv4 == null) ? "n/a" : ipv4;
		ipv6 = (ipv6 == null) ? "n/a" : ipv6;
		String line = String.format("%s   %60s   %15s   %30s\n", dateStr, host, ipv4, ipv6);
		Path logPath = Paths.get(dataDir, fileName);
		try {
			Files.writeString(logPath, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new UpdateLoggerException("Error while writing to zone log file.", e);
		}
	}

}
