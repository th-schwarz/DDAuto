package codes.thischwa.ddauto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	static final String ROLE_LOGVIEWER = "LOGVIEWER";
	static final String ROLE_USER = "USER";

	@Value("${spring.security.user.name}")
	private String userName;

	@Value("${spring.security.user.password}")
	private String password;

	private final DDAutoConfig ddAutoConfig;
	
	private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	public SecurityConfig(DDAutoConfig ddAutoConfig) {
		this.ddAutoConfig = ddAutoConfig;
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
		userManager.createUser(build(userName, password, ROLE_USER));
		if(ddAutoConfig.getZoneLogUserName() != null && ddAutoConfig.getZoneLogUserPassword() != null) {
			userManager.createUser(build(ddAutoConfig.getZoneLogUserName(), ddAutoConfig.getZoneLogUserPassword(), ROLE_LOGVIEWER));
		}
		return userManager;
	}

	private UserDetails build(String user, String password, String role) {
		return User.builder().passwordEncoder(encoder::encode).username(user).password(password).roles(role).build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// disable security for greeting and open-api endpoint
				.authorizeRequests().antMatchers("/", "/favicon.ico", "/v3/api-docs*").permitAll().and()

				// log
				.authorizeRequests().antMatchers("/log").hasAnyRole(ROLE_LOGVIEWER).and()

				// other routes
				.authorizeRequests().anyRequest().hasAnyRole(ROLE_USER).and().httpBasic();

		return http.build();
	}

}
