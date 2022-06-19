package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.GenericIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class MergePropTest extends GenericIntegrationTest {

    @Autowired
    private TestProp prop;

    void tesValue() {
        assertEquals(2, prop.getProp());
    }
}
