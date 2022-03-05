package codes.thischwa.ddauto.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Some network relevant utils.
 */
public class NetUtil {

	public static boolean isIP(String ipStr) {
		return NetUtil.isIPv4(ipStr) || NetUtil.isIPv6(ipStr);
	}

	public static boolean isIPv4(String ipStr) {
		try {
			return (InetAddress.getByName(ipStr) instanceof InetAddress);
		} catch (UnknownHostException e) {
			return false;
		}
	}

	public static boolean isIPv6(String ipStr) {
		try {
			return (InetAddress.getByName(ipStr) instanceof Inet6Address);
		} catch (UnknownHostException e) {
			return false;
		}
	}

	public static String buildBasicAuth(String user, String pwd) {
		String authStr = String.format("%s:%s", user, pwd);
	    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.UTF_8));
	    return "Basic " + base64Creds;
	}

}
