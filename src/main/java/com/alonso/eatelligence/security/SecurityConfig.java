package com.alonso.eatelligence.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/users/exists")
                .access((authentication, context) -> {
                    return new AuthorizationDecision("XMLHttpRequest".equals(context.getRequest().getHeader("X-Requested-With")));
                })
                .requestMatchers(HttpMethod.GET, "/", "/register", "/login", "/registro-exitoso", "/acceso-denegado", "/verificar", "/error")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/validate-client-reg", "/validate-rest-reg", "/validate-login", "/reenviar-verificacion", "/logout")
                .permitAll()
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**"
                )
                .permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout.disable())
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((req, res, authEx) -> res.sendRedirect("/acceso-denegado"))
                .authenticationEntryPoint((req, res, authEx) -> res.sendRedirect("/login"))
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }
}
