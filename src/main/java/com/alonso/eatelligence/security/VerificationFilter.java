package com.alonso.eatelligence.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.IRestauranteService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class VerificationFilter implements Filter {

    private final IRestauranteService restauranteService;

    public VerificationFilter(IRestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    private static final List<String> EXCLUDED_PATHS = List.of(
        "/pending-verification",
        "/logout",
        "/login",
        "/register",
        "/css/", "/js/", "/img/", "/assets/"
    );

    private static final List<String> RUTAS_USUARIO_VERIFICADO = List.of(
        "/settings",
        "/order-history"
    );

    private static final List<String> RUTAS_RESTAURANTE_VERIFICADO = List.of(
        "/admin",
        "/admin/",
        "/admin/dashboard",
        "/admin/plates",
        // "/admin/charts",
        // "/admin/tables",
        "/admin/cooks",
        "/admin/deliverymen"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Usuario usuario) {
            boolean usuarioVerificado = usuario.isVerificado();
            Restaurante restaurante = restauranteService.findByUsuario(usuario).orElse(null);
            boolean restauranteVerificado = restaurante != null && restaurante.isVerificado();
            boolean propietarioVerificado = restaurante != null && restaurante.getPropietario() != null
                                            && restaurante.getPropietario().isVerificado();

            if (!usuarioVerificado &&
                RUTAS_USUARIO_VERIFICADO.stream().anyMatch(path::startsWith)) {
                res.sendRedirect("/pending-verification");
                return;
            }

            if ((!restauranteVerificado || !propietarioVerificado) &&
                RUTAS_RESTAURANTE_VERIFICADO.stream().anyMatch(path::startsWith)) {
                res.sendRedirect("/pending-verification");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}