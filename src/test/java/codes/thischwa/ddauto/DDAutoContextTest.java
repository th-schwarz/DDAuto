package codes.thischwa.ddauto;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = { DDAutoConfig.class, DDAutoContext.class })
@ExtendWith(SpringExtension.class)
class DDAutoContextTest {

	@Autowired
	private DDAutoContext context;

	private Map<String, String> zoneData;

	private Map<String, String> accountData;

	@BeforeEach
	void setUp() {
		zoneData = new HashMap<>();
		zoneData.put("domain.tld", "ns.nameserver.tld");
		accountData = new HashMap<>();
		accountData.put("sld.domain.tld", "1234567890abcdf");
	}

	@Test
	final void dummyTest() {
		Assertions.assertTrue(zoneData.size() == accountData.size());
	}

	@Test
	final void testValidateData_ok() {
		context.validateData(zoneData, accountData);
	}

	@Test
	public void testValidateData_fail1() {
		zoneData.clear();
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			context.validateData(zoneData, accountData);
		});
	}

	@Test
	public void testValidateData_fail2() {
		accountData.put("sld_1.domain1.tld", "1234567890abcdf");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			context.validateData(zoneData, accountData);
		});
	}

}
