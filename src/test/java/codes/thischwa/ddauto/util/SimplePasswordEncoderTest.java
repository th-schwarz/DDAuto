package codes.thischwa.ddauto.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = { "spring.security.salt=3" })
class SimplePasswordEncoderTest {

    @Autowired
    private SimplePasswordEncoder passwordEncoder;

    @Test
    void testEncodeService() {
        assertEquals("dEf4***", passwordEncoder.encode("aBc1"));
    }

    @Test
    void testEncodeDefault() {
        SimplePasswordEncoder enc = new SimplePasswordEncoder();
        assertEquals("aBc1", enc.encode("aBc1"));
    }
}
