package com.alonso.eatelligence.security;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/users/exists")
                .access((authentication, context) -> {
                    return new AuthorizationDecision("XMLHttpRequest".equals(context.getRequest().getHeader("X-Requested-With")));
                })
                .requestMatchers(HttpMethod.GET, "/", "/register", "/login", "/registro-exitoso", "/acceso-denegado", "/verificar")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/validate-client-reg", "/validate-rest-reg", "/validate-login", "/reenviar-verificacion")
                .permitAll()
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**"
                )
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint((req, res, authEx) -> res.sendRedirect("/acceso-denegado"))
            );
        

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }
}
