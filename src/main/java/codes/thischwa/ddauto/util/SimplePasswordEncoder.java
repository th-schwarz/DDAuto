package codes.thischwa.ddauto.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class SimplePasswordEncoder implements PasswordEncoder {

    @Value("${spring.security.salt:0}")
    private int salt;

    @Override
    public String encode(CharSequence rawPassword) {
        StringBuilder pwd = new StringBuilder(rawPassword.length());
        rawPassword.chars().forEach(c -> pwd.append((char)(c + salt)));
        IntStream.range(0, salt).forEach(c -> pwd.append('*'));
        return pwd.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).endsWith(encodedPassword);
    }
}
