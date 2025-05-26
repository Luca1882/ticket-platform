package it.ticket.platform.ticket_platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private DatabaseUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/ticket/create", "/ticket/edit", "/ticket/delete")
                .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/dashboard")
                .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/**")
                .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/ticket/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .requestMatchers("/").permitAll()
                .and().formLogin()
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if (role.equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/ticket");
                    }
                    System.out.println("Login riuscito! Ruolo: " + role);
                    System.out.println("Redirect a: " + (role.equals("ROLE_ADMIN") ? "/admin/dashboard" : "/ticket"));
                })
                .and().exceptionHandling()
                .and().logout()
                .and().csrf().disable();

        return http.build();
    }

    @Bean
    public DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
