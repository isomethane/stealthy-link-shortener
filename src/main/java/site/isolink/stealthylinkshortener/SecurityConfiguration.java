package site.isolink.stealthylinkshortener;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
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
            .authorizeHttpRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint().excluding("health", "info")).hasRole("ADMIN")
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
            .build();
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer(
        @Value("${server.http.port}") int httpPort,
        @Value("${server.port}") int httpsPort
    ) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector(httpPort, httpsPort));
        return tomcat;
    }

    private Connector redirectConnector(int httpPort, int httpsPort) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);

        return connector;
    }
}
