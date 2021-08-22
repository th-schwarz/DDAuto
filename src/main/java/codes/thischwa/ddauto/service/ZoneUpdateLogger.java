package codes.thischwa.ddauto.service;

/**
 * Interface for logging zone updates.
 */
public interface ZoneUpdateLogger {

	public void log(String host, String ipv4, String ipv6) throws UpdateLoggerException;
}
