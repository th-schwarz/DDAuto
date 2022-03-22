package codes.thischwa.ddauto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${spring.security.user.name}")
	private String user;

	@Value("${spring.security.user.password}")
	private String password;
	
	@Autowired
	private DDAutoConfig ddAutoConfig;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.inMemoryAuthentication().withUser(user).password(encoder.encode(password)).roles("USER");
		if(ddAutoConfig.getZoneLogUserName() != null && ddAutoConfig.getZoneLogUserPassword() != null) {
			auth.inMemoryAuthentication().withUser(ddAutoConfig.getZoneLogUserName()).password(encoder.encode(ddAutoConfig.getZoneLogUserPassword()))
			.roles("LOGVIEWER");
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		// disable security for greeting and open-api endpoint
		.authorizeRequests().antMatchers("/", "/favicon.ico", "/v3/api-docs*").permitAll().and()
		
		// log
		.authorizeRequests().antMatchers("/log").hasAnyRole("LOGVIEWER").and()
		
		// other routes
		.authorizeRequests().anyRequest().hasAnyRole("USER").and().httpBasic();
	}
	
}
