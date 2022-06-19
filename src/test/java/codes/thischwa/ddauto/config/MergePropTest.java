package codes.thischwa.ddauto.config;

import codes.thischwa.ddauto.GenericIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MergePropTest extends GenericIntegrationTest {

    @Autowired
    private TestProp prop;

    @Test
    void testValue() {
        assertEquals(2, prop.getProp());
    }
}
