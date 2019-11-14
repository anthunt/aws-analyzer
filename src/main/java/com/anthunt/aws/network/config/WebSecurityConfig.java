package com.anthunt.aws.network.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
    public void configure(WebSecurity web) {
        web
        	.ignoring()
    			.antMatchers("/favicon.ico")
        		.antMatchers("/css/**/*")
        		.antMatchers("/js/**/*")
        		.antMatchers("/img/**/*")
        		.antMatchers("/");
    }
	
	@Override	
	protected void configure(HttpSecurity http) throws Exception {
	    http
	    	.csrf().disable()
	        .formLogin()
	            .loginPage("/login")
	            .defaultSuccessUrl("/profiles", true)
				.failureUrl("/login?error")
	            .permitAll()
	            .and()
	        .logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.and()
			.authorizeRequests()
    			.antMatchers("/favicon.ico").permitAll()
	    		.antMatchers("/css/**/*").permitAll()
	    		.antMatchers("/js/**/*").permitAll()
	    		.antMatchers("/img/**/*").permitAll()
	    		.antMatchers("/").permitAll()
				.anyRequest().hasRole("USER")
				.and()
			.sessionManagement()
				.maximumSessions(1)
				.maxSessionsPreventsLogin(true)
				.expiredUrl("/")
			;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	  return new BCryptPasswordEncoder();
	}
	
	@Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
	
}
