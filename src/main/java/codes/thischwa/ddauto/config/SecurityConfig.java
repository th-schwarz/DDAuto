package codes.thischwa.ddauto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	static final String ROLE_LOGVIEWER = "LOGVIEWER";
	static final String USER = "USER";

	@Value("${spring.security.user.name}")
	private String userName;

	@Value("${spring.security.user.password}")
	private String password;
	
	private final DDAutoConfig ddAutoConfig;
	
	public SecurityConfig(DDAutoConfig ddAutoConfig) {
		this.ddAutoConfig = ddAutoConfig;
	}
		
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        User.UserBuilder userBuilder = User.builder();
            userBuilder.username(userName).password(password)
            .roles(USER);
        InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();
        userManager.createUser(userBuilder.build());
        if(ddAutoConfig.getZoneLogUserName() != null && ddAutoConfig.getZoneLogUserPassword() != null) {
			userBuilder.username(ddAutoConfig.getZoneLogUserName())
					.password(ddAutoConfig.getZoneLogUserPassword()).roles(ROLE_LOGVIEWER);
			userManager.createUser(userBuilder.build());
		}
        return userManager;
    }

	@SuppressWarnings("deprecation")
	@Bean
    public PasswordEncoder passwordEncoder() {
      return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		// disable security for greeting and open-api endpoint
		.authorizeRequests().antMatchers("/", "/favicon.ico", "/v3/api-docs*").permitAll().and()

		// log
		.authorizeRequests().antMatchers("/log").hasAnyRole(ROLE_LOGVIEWER).and()

		// other routes
		.authorizeRequests().anyRequest().hasAnyRole(USER).and().httpBasic();

		return http.build();
	}
	
}
