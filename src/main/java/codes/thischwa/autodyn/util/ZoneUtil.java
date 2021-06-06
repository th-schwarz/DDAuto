package codes.thischwa.autodyn.util;

import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;

public class ZoneUtil {

	private final static long DEFAULT_TLD = 60;

	public static void addOrUpdateIPv4(Zone zone, String sld, String ip) {
		addOrUpdateIP(zone, sld, ip, "A");
	}

	public static void addOrUpdateIPv6(Zone zone, String sld, String ip) {
		addOrUpdateIP(zone, sld, ip, "AAAA");
	}
	
	public static void removeIPv4(Zone zone, String sld) {
		removeIP(zone, sld, "A");
	}
	
	public static void removeIPv6(Zone zone, String sld) {
		removeIP(zone, sld, "AAAA");
	}

	private static void removeIP(Zone zone, String sld, String type) {
		ResourceRecord rr = searchResourceRecord(zone, sld, type);
		if(rr != null) {
			zone.getResourceRecords().remove(rr);
		}
	}
	
	private static void addOrUpdateIP(Zone zone, String sld, String ip, String type) {
		ResourceRecord rr = searchResourceRecord(zone, sld, type);
		if(rr != null) {
			rr.setValue(ip);
			rr.setTtl(DEFAULT_TLD);
		} else {
			ResourceRecord rrSld = new ResourceRecord();
			rrSld.setName(sld);
			rrSld.setValue(ip);
			rrSld.setType(type);
			rrSld.setTtl(DEFAULT_TLD);
			zone.getResourceRecords().add(rrSld);
		}
	}

	public static ResourceRecord searchResourceRecord(Zone zone, String name, String type) {
		for(ResourceRecord rr : zone.getResourceRecords()) {
			if(rr.getType().equals(type) && rr.getName().equals(name)) {
				return rr;
			}
		}
		return null;
	}

}
