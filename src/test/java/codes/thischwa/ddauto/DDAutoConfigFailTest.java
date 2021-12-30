package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class })
@ExtendWith(SpringExtension.class)
class DDAutoConfigFailTest {

	@Autowired
	private DDAutoConfig config;

	@Test
	final void testWrongHostFormat() {
		String wrongHost = "wrong-formatted.host";
		DDAutoConfig.Zone z = config.getZones().get(0);
		z.getHosts().add(wrongHost);
		assertThrows(IllegalArgumentException.class, () -> {
			config.readData();
		});
		z.getHosts().remove(wrongHost);
	}

	@Test
	final void testEmptyHosts() {
		DDAutoConfig.Zone z = config.getZones().get(1);
		List<String> hosts = new ArrayList<>(z.getHosts());
		z.getHosts().clear();
		assertThrows(IllegalArgumentException.class, () -> {
			config.readData();
		});
		z.setHosts(hosts);
	}

}
