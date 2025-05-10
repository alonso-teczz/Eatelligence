package com.alonso.eatelligence.security;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alonso.eatelligence.service.IDireccionService;
import com.alonso.eatelligence.service.IRestauranteService;
import com.alonso.eatelligence.utils.GeoUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RestauranteAccessFilter extends OncePerRequestFilter {

  private static final Logger logger = LogManager.getLogger(RestauranteAccessFilter.class);

  @Autowired private IDireccionService direccionService;
  @Autowired private IRestauranteService restauranteService;

  /**
   * Filtro que se encarga de verificar que el usuario tenga una
   * dirección de envío establecida en la sesión y que la distancia
   * entre la dirección de envío y el restaurante sea inferior a 15 km.
   * Si no se cumple esta condición, se redirige al usuario a la página
   * de inicio.
   *
   * @param req   la petición HTTP
   * @param res   la respuesta HTTP
   * @param chain el encadenamiento de filtros
   * @throws ServletException si se produce un error al procesar la petición
   * @throws IOException       si se produce un error al enviar la respuesta
   */
  @Override
  protected void doFilterInternal(
    HttpServletRequest req,
    HttpServletResponse res,
    FilterChain chain
  ) throws ServletException, IOException {
    String uri = req.getRequestURI();
    logger.debug("Interceptando petición URI: {}", uri);

    if (uri.matches("/restaurants/\\d+")) {
      logger.debug("Petición a página de restaurante detectada.");

      // 1) Comprobar login
      var auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
          logger.info("Usuario no autenticado. Redirigiendo a /login");
          res.sendRedirect(req.getContextPath() + "/login");
          return;
      }

      HttpSession session = req.getSession(false);
      Long dirId = (session != null) ? (Long) session.getAttribute("direccionEnvioId") : null;
      logger.debug("ID dirección de envío en sesión: {}", dirId);

      if (dirId != null) {
        var dir = direccionService.getById(dirId).orElse(null);
        var restoId = Long.valueOf(uri.substring(uri.lastIndexOf('/') + 1));
        var resto  = restauranteService.findById(restoId).orElse(null);

        if (dir == null) {
          logger.warn("No se encontró dirección con ID {}", dirId);
        }
        if (resto == null) {
          logger.warn("No se encontró restaurante con ID {}", restoId);
        }

        if (dir != null && resto != null) {
          Double lat1 = dir.getLatitud();
          Double lon1 = dir.getLongitud();
          Double lat2 = resto.getDireccion().getLatitud();
          Double lon2 = resto.getDireccion().getLongitud();

          logger.debug("Coordenadas cliente: ({}, {})", lat1, lon1);
          logger.debug("Coordenadas restaurante: ({}, {})", lat2, lon2);

          if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            logger.warn("Coordenadas nulas detectadas, omitiendo filtro de distancia.");
          } else {
            double distancia = GeoUtils.haversine(lat1, lon1, lat2, lon2);
            logger.info("Distancia calculada: {} km", distancia);

            if (distancia > 15.0) {
              logger.info("Distancia mayor a 15 km. Redirigiendo a home.");
              res.sendRedirect(req.getContextPath() + "/");
              return;
            }
          }
        }
      } else {
        logger.info("No hay dirección de envío en sesión. Permitiendo el acceso.");
      }
    }

    // Continuar flujo
    chain.doFilter(req, res);
  }
}
