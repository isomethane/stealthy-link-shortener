package site.isolink.stealthylinkshortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
        PasswordEncoder encoder,
        @Value("${admin.name}") String adminName,
        @Value("${admin.password}") String adminPassword
    ) {
        return new InMemoryUserDetailsManager(List.of(
            new User(adminName, encoder.encode(adminPassword), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint().excluding("health", "info")).hasRole("ADMIN")
                .antMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
            .build();
    }
}
