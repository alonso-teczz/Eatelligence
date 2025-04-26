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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, VerificationFilter verificationFilter) throws Exception {

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .addFilterBefore(new NoCacheFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(verificationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**")
                .access((authentication, context) -> {
                    return new AuthorizationDecision("XMLHttpRequest".equals(context.getRequest().getHeader("X-Requested-With")));
                })
                .requestMatchers(HttpMethod.GET,
                    "/",
                    "/register",
                    "/registro-exitoso",
                    "/verificar",
                    "/login",
                    "/acceso-denegado",
                    "/verificacion-pendiente"
                )
                .permitAll()
                .requestMatchers(HttpMethod.POST,
                    "/validate-client-reg",
                    "/validate-rest-reg",
                    "/reenviar-verificacion",
                    "/validate-login",
                    "/logout"
                )
                .permitAll()
                .requestMatchers("/settings", "/historial").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/rutas").hasRole("REPARTIDOR")
                .requestMatchers("/pedidos").hasRole("COCINERO")
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/img/**",
                    "/error/**"
                )
                .permitAll()
                .anyRequest().denyAll()
            )
            .formLogin(form -> form.disable())
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
