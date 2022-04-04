package codes.thischwa.ddauto.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Some network relevant utils.
 */
public interface NetUtil {

	static boolean isIP(String ipStr) {
		return NetUtil.isIPv4(ipStr) || NetUtil.isIPv6(ipStr);
	}

	static boolean isIPv4(String ipStr) {
		try {
			return (InetAddress.getByName(ipStr) instanceof Inet4Address);
		} catch (UnknownHostException e) {
			return false;
		}
	}

	static boolean isIPv6(String ipStr) {
		try {
			return (InetAddress.getByName(ipStr) instanceof Inet6Address);
		} catch (UnknownHostException e) {
			return false;
		}
	}

	static String buildBasicAuth(String user, String pwd) {
		String authStr = String.format("%s:%s", user, pwd);
	    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.UTF_8));
	    return "Basic " + base64Creds;
	}
	
	static String getBaseUrl(boolean forceHttps) {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
		if(forceHttps)
			builder.scheme("https");
		return builder.replacePath(null)
				.build()
		        .toUriString();
	}
}
