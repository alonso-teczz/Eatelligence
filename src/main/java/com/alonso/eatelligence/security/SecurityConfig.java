package com.alonso.eatelligence.security;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .addFilterBefore(noCacheFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET,
                    "/api/users/exists"
                )
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
                    "/settings",
                    "/historial",
                    "/admin",
                    "/rutas",
                    "/pedidos"
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
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/error/**"
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

    @Bean Filter noCacheFilter() {
        return new OncePerRequestFilter() {
            private final List<String> paths = List.of(
                "/",
                "/login",
                "/register",
                "/registro-exitoso"
            );

            @Override
            protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
            ) throws ServletException, IOException {
                String path = request.getRequestURI();

                if (paths.contains(path)) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }
}
