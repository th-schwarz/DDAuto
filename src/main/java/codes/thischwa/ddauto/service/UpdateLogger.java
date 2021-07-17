package codes.thischwa.ddauto.service;

public interface UpdateLogger {

	public void log(String host, String ipv4, String ipv6) throws UpdateLoggerException;
}
