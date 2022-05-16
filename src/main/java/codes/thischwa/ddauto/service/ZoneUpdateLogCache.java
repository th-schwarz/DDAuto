package codes.thischwa.ddauto.service;

import codes.thischwa.ddauto.config.DDAutoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * A cache to hold the zone update logs.
 */
@Service
public class ZoneUpdateLogCache implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ZoneUpdateLogCache.class);

    @Autowired
    private DDAutoConfig conf;

    private List<ZoneLogItem> zoneUpdateItems = new CopyOnWriteArrayList<>();

    private DateTimeFormatter dateTimeFormatter;

    public boolean enabled() {
        return conf.isZoneLogPageEnabled() && conf.getZoneLogFilePattern() != null;
    }

    public int size() {
        return zoneUpdateItems.size();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!enabled()) {
            logger.info("Log page is disabled or ddauto.zone.log-file-pattern isn't set, prefill is canceled.");
            return;
        }

        dateTimeFormatter = DateTimeFormatter.ofPattern(conf.getZoneLogDatePattern());

        // build location pattern, if no url type is found 'file:' will be assumed
        String locPattern = (conf.getZoneLogFilePattern().contains(":")) ? conf.getZoneLogFilePattern()
                : "file:" + conf.getZoneLogFilePattern();
        logger.debug("Using the following log file pattern: {}", locPattern);

        List<String> logEntries = new ArrayList<>();
        Resource[] logs = new PathMatchingResourcePatternResolver().getResources(locPattern);
        if (logs.length == 0) {
            logger.debug("No log files found.");
            return;
        }
        for (Resource log : logs) {
            String filename = log.getFilename();
            if (filename != null && (filename.endsWith(".log") || filename.endsWith(".gz"))) {
                readResource(log, logEntries);
            }
        }

        // ordering and parsing, must be asc because new items will be added at the end
        logEntries.sort(null);
        Pattern pattern = Pattern.compile(conf.getZoneLogPattern());
        zoneUpdateItems = logEntries.stream().map(i -> parseLogEntry(i, pattern)).filter(Objects::nonNull)
                .sorted(Comparator.comparing(ZoneLogItem::getDateTime))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        logger.info("{} log entries successful read and parsed.", zoneUpdateItems.size());
    }

    public void addLogEntry(String host, String ipv4, String ipv6) {
        String now = dateTimeFormatter.format(LocalDateTime.now());
        ZoneLogItem item = new ZoneLogItem(now, host, ipv4, ipv6);
        zoneUpdateItems.add(item);
    }

    public List<ZoneLogItem> getItems() {
        return zoneUpdateItems;
    }

    public ZoneLogPage getResponseAll() {
        ZoneLogPage logs = new ZoneLogPage();
        logs.setPageSize(conf.getZoneLogPageSize());
        logs.setTotal(zoneUpdateItems.size());
        logs.setItems(zoneUpdateItems.stream().sorted(Comparator.comparing(ZoneLogItem::getDateTime, Comparator.reverseOrder())).collect(Collectors.toList()));
        return logs;
    }

    public ZoneLogPage getResponsePage(Integer page, String search) {
        logger.debug("Entered #getResponsePage with: page={}, search={}", page, search);
        if (page == null || page == 0)
            page = 1;
        ZoneLogPage lp = new ZoneLogPage();
        lp.setPage(page);

        // searching in host und timestamp
        List<ZoneLogItem> items = (search == null || search.isEmpty()) ? new ArrayList<>(zoneUpdateItems)
                : zoneUpdateItems.stream().filter(i -> i.getHost().contains(search) || i.getDateTime().contains(search))
                .collect(Collectors.toList());

        // respect ordering of dateTime, must be desc!
        items = items.stream().sorted(Comparator.comparing(ZoneLogItem::getDateTime, Comparator.reverseOrder())).collect(Collectors.toList());

        int currentIdx = (conf.getZoneLogPageSize() * page) - conf.getZoneLogPageSize();
        List<ZoneLogItem> pageItems = new ArrayList<>();
        int nextIdx = currentIdx + conf.getZoneLogPageSize();
        for (int i = currentIdx; i < nextIdx; i++) {
            if (i >= items.size())
                break;
            pageItems.add(items.get(i));
        }

        lp.setPageSize(conf.getZoneLogPageSize());
        lp.setItems(pageItems);
        lp.setTotalPage(((zoneUpdateItems.size() - 1) / conf.getZoneLogPageSize()) + 1);
        lp.setTotal(items.size());
        return lp;
    }

    ZoneLogItem parseLogEntry(String logEntry, Pattern pattern) {
        if (logEntry == null)
            return null;
        Matcher matcher = pattern.matcher(logEntry);
        if (matcher.matches() && matcher.groupCount() == 4) {
            return new ZoneLogItem(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        return null;
    }

    private void readResource(Resource res, List<String> logItems) {
        logger.debug("Process log zone update file: {}", res.getFilename());
        String filename = res.getFilename();
        if (filename == null)
            return;
        try (InputStream in = filename.endsWith(".gz") ? new GZIPInputStream(res.getInputStream()) : res.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (reader.ready()) {
                logItems.add(reader.readLine());
            }
        } catch (IOException e) {
            logger.error("Couldn't process log zone update file: {}", filename);
            throw new IllegalArgumentException("Couldn't read: " + filename);
        }
    }

}