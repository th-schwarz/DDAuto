package codes.thischwa.ddauto.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import codes.thischwa.ddauto.config.DDAutoConfig;

/**
 * A cache to hold the zone update logs
 *
 */
@Service
public class ZoneUpdateLogCache implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(ZoneUpdateLogCache.class);

	@Autowired
	private DDAutoConfig conf;
	
	private List<ZoneUpdateItem> zoneUpdateItems = new CopyOnWriteArrayList<>();
	
	private DateTimeFormatter dateTimeFormatter;

	public boolean enabled() {
		return conf.isLogPageEnabled() && conf.getZoneLogFilePattern() != null;
	}

	public int length() {
		return zoneUpdateItems.size();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(!enabled()) {
			logger.info("Log page is disabled or ddauto.zone.log-file-pattern isn't set, prefill is canceled.");
			return;
		}
		
		dateTimeFormatter = DateTimeFormatter.ofPattern(conf.getZoneLogDatePattern());
		
		// build location pattern
		String locPattern = (conf.getZoneLogFilePattern().contains(":")) ? conf.getZoneLogFilePattern() : "file:" + conf.getZoneLogFilePattern();
		logger.debug("Using the following log file pattern: {}", locPattern);

		List<String> logEntries = new ArrayList<>();
		Resource[] logs = new PathMatchingResourcePatternResolver().getResources(locPattern);
		if(logs == null || logs.length == 0) {
			logger.debug("No log files found.");
			return;
		}
		for(Resource log : logs) {
			String filename = log.getFilename();
			if(filename.endsWith(".log") || filename.endsWith(".gz")) {
				read(log, logEntries);
			}
		}
		
		// ordering and parsing
		logEntries.sort(null);
		Pattern pattern = Pattern.compile(conf.getZoneLogPattern());
		zoneUpdateItems = new CopyOnWriteArrayList<ZoneUpdateItem>(logEntries.stream()
				.map(i -> parseLogEntry(i, pattern))
				.filter(i -> i != null)
				.collect(Collectors.toList()));
		logger.debug("{} log entries successful read and parsed.", zoneUpdateItems.size());
	}
	
	public void addLogEntry(String host, String ipv4, String ipv6) {
		String now = dateTimeFormatter.format(LocalDateTime.now());
		ZoneUpdateItem item = new ZoneUpdateItem(now, host, ipv4, ipv6);
		zoneUpdateItems.add(item);
	}
	
	public List<ZoneUpdateItem> get() {
		return zoneUpdateItems;
	}
	
	public LogWrapper getResponseAll() {
		LogWrapper logs = new LogWrapper();
		logs.setTotal(zoneUpdateItems.size());
		logs.setItems(zoneUpdateItems);
		return logs;
	}

	ZoneUpdateItem parseLogEntry(String logEntry, Pattern pattern) {
		if(logEntry == null)
			return null;
		Matcher matcher = pattern.matcher(logEntry);
		if(matcher.matches() && matcher.groupCount() == 4) {
			return new ZoneUpdateItem(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
		}
		return null;
	}
	
	private void read(Resource res, List<String> logItems) {
		logger.debug("Process log zone update file: {}", res.getFilename());
		try {
			InputStream in = res.getFilename().endsWith(".gz") ? new GZIPInputStream(res.getInputStream()) : res.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while(reader.ready()) {
				logItems.add(reader.readLine());
			}
		} catch (IOException e) {
			logger.error("Couldn't process log zone update file: {}", res.getFilename());
			throw new IllegalArgumentException("Couldn't read: " + res.getFilename());
		}
	}

}