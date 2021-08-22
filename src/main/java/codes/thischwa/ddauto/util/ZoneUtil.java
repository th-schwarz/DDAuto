package codes.thischwa.ddauto.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.domainrobot.sdk.models.generated.ResourceRecord;
import org.domainrobot.sdk.models.generated.Zone;

/**
 * A static utility class mainly for the Zone object of the domain-robot sdk.
 */
public abstract class ZoneUtil {

	private static final long DEFAULT_TLD = 60;

	private ZoneUtil() {
	}

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
		Optional<ResourceRecord> rrO = zone.getResourceRecords().stream()
				.filter(rr -> rr.getType().equals(type) && rr.getName().equals(name)).findFirst();
		return rrO.isPresent() ? rrO.get() : null;
	}

	public static String deriveZone(String host) {
		long cnt = host.chars().filter(ch -> ch == '.').count();
		if(cnt < 2)
			throw new IllegalArgumentException("'host' must be a sub domain.");
		return host.substring(host.indexOf(".") + 1);
	}

	public static boolean isValidateIP(String ipStr) {
		try {
			InetAddress.getByName(ipStr);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	public static boolean isIPv6(String ipStr) throws IllegalArgumentException {
		try {
			return (InetAddress.getByName(ipStr) instanceof Inet6Address);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(ipStr + " isn't a valid IP.");
		}
	}
}
