package codes.thischwa.ddauto.service;

public class ZoneLogItem implements Comparable<ZoneLogItem> {

	private String dateTime;

	private String host;

	private String ipv4;

	private String ipv6;
		
	ZoneLogItem(String dateTime, String host, String ipv4, String ipv6) {
		this.dateTime = dateTime;
		this.host = host;
		this.ipv4 = ipv4 == null ? "n/a" : ipv4;
		this.ipv6 = ipv6  == null ? "n/a" : ipv6;
	}

	public String getDateTime() {
		return dateTime;
	}
	
	public String getHost() {
		return host;
	}

	public String getIpv4() {
		return ipv4;
	}

	public String getIpv6() {
		return ipv6;
	}

	@Override
	public String toString() {
		return "ZoneLogItem [dateTime=" + dateTime + ", host=" + host + ", ipv4=" + ipv4 + ", ipv6=" + ipv6 + "]";
	}

	@Override
	public int compareTo(ZoneLogItem o2) {
		return dateTime.compareTo(o2.getDateTime());
	}

	
	
}