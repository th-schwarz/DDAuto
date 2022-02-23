package codes.thischwa.ddauto.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ZoneUpdateItem {

	@JsonIgnore
	private String dateTime;

	@JsonIgnore
	private String host;

	@JsonIgnore	
	private String ipv4;

	@JsonIgnore
	private String ipv6;
	
	private String item;
		
	ZoneUpdateItem(String dateTime, String host, String ipv4, String ipv6) {
		this.dateTime = dateTime;
		this.host = host;
		this.ipv4 = ipv4 == null ? "n/a" : ipv4;
		this.ipv6 = ipv6  == null ? "n/a" : ipv6;
		this.item = String.format("%s,%s,%s,%s", dateTime, host, ipv4, ipv6);
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
	
	public String getItem() {
		return item;
	}

	@Override
	public String toString() {
		return "ZoneUpdateItem [dateTime=" + dateTime + ", host=" + host + ", ipv4=" + ipv4 + ", ipv6=" + ipv6 + "]";
	}
	
}