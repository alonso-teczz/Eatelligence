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

    // rutas que solo usuarios verificados pueden acceder
    private static final List<String> RUTAS_USUARIO_VERIFICADO = List.of(
        "/orders/checkout"
    );

    private static final List<String> RUTAS_RESTAURANTE_VERIFICADO = List.of(
        "/admin",
        "/admin/",
        "/admin/dashboard",
        "/admin/plates",
        "/admin/cooks",
        "/admin/deliverymen"
    );

    /**
     * Verifica si el usuario y su restaurante (si lo tiene) están verificados
     * antes de permitir el acceso a ciertas rutas.
     *
     * <ul>
     * <li>Las rutas <code>/orders/checkout</code> solo pueden ser accedidas por
     * usuarios verificados.</li>
     * <li>Las rutas del panel de administración solo pueden ser accedidas por
     * restaurantes verificados y su propietario.</li>
     * </ul>
     * Si el usuario no cumple con las condiciones, se redirige a la página de
     * verificación pendiente.
     *
     * @param request  el objeto de solicitud HTTP.
     * @param response el objeto de respuesta HTTP.
     * @param chain    el objeto de filtrado.
     * @throws IOException      si ocurre un error de E/S.
     * @throws ServletException si ocurre un error en el filtrado.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // deja pasar recursos públicos
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

            // acceso a resumen de compra y confirmación solo usuarios verificados
            if (!usuarioVerificado &&
                RUTAS_USUARIO_VERIFICADO.stream().anyMatch(path::startsWith)) {
                res.sendRedirect("/pending-verification");
                return;
            }

            // acceso a panel admin solo restaurantes verificados
            if ((!restauranteVerificado || !propietarioVerificado) &&
                RUTAS_RESTAURANTE_VERIFICADO.stream().anyMatch(path::startsWith)) {
                res.sendRedirect("/pending-verification");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
