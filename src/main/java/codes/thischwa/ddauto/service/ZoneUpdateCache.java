package codes.thischwa.ddauto.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class ZoneUpdateCache {

	private static final Logger logger = LoggerFactory.getLogger(ZoneUpdateCache.class);

	@Value("${zone.log-pattern:(.*) - (.*) (.*)}")
	private String zoneLogPattern;

	@Value("${zone.log-file-pattern}")
	private String zoneLogFilePattern;

	private List<ZoneUpdateItem> zoneUpdateItems = new CopyOnWriteArrayList<>();

	public boolean enabled() {
		return zoneLogFilePattern != null;
	}

	public void prefill() throws IOException {
		if(!enabled()) {
			logger.info("No log-pattern set, prefill is canceled.");
			return;
		}

		List<String> logEntries = new ArrayList<>();
		Resource[] logs = new PathMatchingResourcePatternResolver().getResources("file:" + zoneLogFilePattern);
		for(Resource log : logs) {
			String filename = log.getFilename();
			logger.debug("found: {}", filename);
			if(filename.endsWith(".log") || filename.endsWith(".gz")) {
				read(log, logEntries);
			}
		}
		
		// ordering and parsing
		logEntries.sort(null);
		Pattern pattern = Pattern.compile(zoneLogPattern);
		zoneUpdateItems = new CopyOnWriteArrayList<ZoneUpdateItem>(logEntries.stream()
				.map(i -> parseLogEntry(i, pattern))
				.filter(i -> i != null)
				.collect(Collectors.toList()));
		logger.debug("{} log entries successful read and parsed.", zoneUpdateItems.size());
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