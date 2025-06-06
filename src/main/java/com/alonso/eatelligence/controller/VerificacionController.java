package com.alonso.eatelligence.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.email.EmailService;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;
import com.alonso.eatelligence.service.imp.RestauranteServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.service.imp.VerificationTokenServiceImp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VerificacionController {

    private final VerificationTokenServiceImp tokenService;
    private final UsuarioServiceImp usuarioService;
    private final RestauranteServiceImp restauranteService;
    private final EmailService emailService;

    @GetMapping("/verify")
    @Transactional
    public String verificar(@RequestParam("token") String token, Model model) {
        VerificationToken vt = this.tokenService.findByToken(token).orElse(null);

        if (vt == null || vt.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "El token es inválido o ha expirado.");
            return "feedback/verificacionFallida";
        }

        switch (vt.getTipo()) {
            case USUARIO -> {
                Usuario usuario = vt.getUsuario();
                usuario.setVerificado(true);
                this.usuarioService.save(usuario);
                model.addAttribute("nombre", usuario.getNombre());
            }
            case RESTAURANTE -> {
                Restaurante restaurante = vt.getRestaurante();
                restaurante.setVerificado(true);
                this.restauranteService.save(restaurante);
                model.addAttribute("nombre", restaurante.getNombreComercial());
            }
        }

        this.tokenService.delete(vt);
        return "feedback/verificacionExitosa";
    }

    @PostMapping("/resend-verification")
    @ResponseBody
    public ResponseEntity<?> reenviarVerificacion(
        @RequestParam("token") String token,
        @RequestParam("tipo") String tipo
    ) {
        Optional<VerificationToken> optToken = this.tokenService.findByToken(token);
        if (optToken.isEmpty()) return ResponseEntity.badRequest().body("Token inválido");
    
        VerificationToken oldVT = optToken.get();
    
        if (oldVT.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            Long segundosRestantes = Duration.between(LocalDateTime.now(), oldVT.getFechaExpiracion()).toSeconds();
            return ResponseEntity.status(429)
                .body("Tu último token aún no ha expirado. Debes esperar " + 
                    (segundosRestantes >= 60 
                        ? (segundosRestantes / 60 > 1 
                            ? (segundosRestantes / 60) + " minutos" 
                            : "1 minuto") 
                        : "menos de un minuto") + 
                    " para solicitar otro correo de verificación. Revisa tu bandeja de entrada.");
        }        
    
        Integer intentos = oldVT.getIntentosReenvio();
        Long minutosEspera = (long) Math.pow(2, intentos) * 5;
    
        if (oldVT.getUltimoIntento() != null &&
            oldVT.getUltimoIntento().plusMinutes(minutosEspera).isAfter(LocalDateTime.now())
        ) {
            Long segundosRestantes = Duration.between(LocalDateTime.now(), oldVT.getUltimoIntento().plusMinutes(minutosEspera)).toSeconds();
        
            return ResponseEntity.status(429)
                .body("Tu último token aún no ha expirado. Debes esperar " + 
                    (segundosRestantes >= 60 
                        ? (segundosRestantes / 60 > 1 
                            ? (segundosRestantes / 60) + " minutos" 
                            : "1 minuto") 
                        : "menos de un minuto") + 
                    " para solicitar otro correo de verificación. Revisa tu bandeja de entrada.");
        }
    
        try {
            switch (tipo.toUpperCase()) {
                case "USUARIO" -> {
                    Usuario u = oldVT.getUsuario();
                    if (u == null) return ResponseEntity.badRequest().body("Token no asociado a un usuario");
                    this.tokenService.delete(oldVT);
    
                    VerificationToken newVT = tokenService.forUser(u, intentos++);
                    newVT.setIntentosReenvio(intentos + 1);
                    newVT.setUltimoIntento(LocalDateTime.now());
                    newVT = tokenService.save(newVT);
    
                    this.emailService.sendEmail(
                        u.getEmail(),
                        "Eatelligence - Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", u.getNombre(),
                            "urlVerificacion", "https://eatelligence.up.railway.app/verify?token=" + newVT.getToken(),
                            "proximoIntento", minutosEspera * 2 + " minutos"
                        )
                    );
                }
    
                case "RESTAURANTE" -> {
                    Restaurante r = oldVT.getRestaurante();
                    if (r == null) return ResponseEntity.badRequest().body("Token no asociado a un restaurante");
                    this.tokenService.delete(oldVT);
    
                    VerificationToken newVT = tokenService.forRestaurant(r, intentos++);
                    newVT.setIntentosReenvio(intentos + 1);
                    newVT.setUltimoIntento(LocalDateTime.now());
                    newVT = tokenService.save(newVT);
    
                    this.emailService.sendEmail(
                        r.getEmailEmpresa(),
                        "Eatelligence - Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", r.getNombreComercial(),
                            "urlVerificacion", "https://eatelligence.up.railway.app/verify?token=" + newVT.getToken(),
                            "proximoIntento", minutosEspera * 2 + " minutos"
                        )
                    );
                }
    
                default -> {
                    return ResponseEntity.badRequest().body("Tipo de token inválido");
                }
            }
    
            return ResponseEntity.ok("Correo de verificación reenviado correctamente. Podrás solicitar el siguiente en " + (minutosEspera * 2) + " minutos.");
    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al reenviar el correo: " + e.getMessage());
        }
    }

    @GetMapping("/pending-verification")
    public String mostrarPaginaVerificacion(
        @SessionAttribute(value = "restaurante", required = false) Restaurante restaurante,
        @SessionAttribute(value = "usuario", required = false) Usuario usuario,
        Model model
    ) {
        // Sin sesión: redirigir
        if (restaurante == null && usuario == null) {
            return "redirect:/login";
        }
    
        // Sesión como restaurante
        if (restaurante != null) {
            Usuario propietario = restaurante.getPropietario();
            boolean restVerificado = restaurante.isVerificado();
            boolean propietarioVerificado = propietario != null && propietario.isVerificado();
    
            VerificationToken tokenRestaurante = tokenService.findByRestaurante(restaurante).orElse(null);
            VerificationToken tokenPropietario = (propietario != null)
                ? tokenService.findByUsuario(propietario).orElse(null)
                : null;
    
            // Si ambos están verificados, redirigir
            if (restVerificado && propietarioVerificado) {
                return "redirect:/";
            }
    
            model.addAttribute("tipo", "RESTAURANTE");
            model.addAttribute("verificadoRestaurante", restVerificado);
            model.addAttribute("verificadoPropietario", propietarioVerificado);
    
            if (tokenRestaurante != null) model.addAttribute("tokenRestaurante", tokenRestaurante);
            if (tokenPropietario != null) model.addAttribute("tokenPropietario", tokenPropietario);
    
            return "feedback/verificacionPendiente";
        }
    
        // Sesión como usuario
        if (usuario != null) {
            if (usuario.isVerificado()) {
                return "redirect:/";
            }
    
            VerificationToken lastToken = tokenService.findByUsuario(usuario).orElse(null);
    
            model.addAttribute("tipo", "USUARIO");
            model.addAttribute("verificadoUsuario", false);
    
            if (lastToken != null) model.addAttribute("lastToken", lastToken);
    
            return "feedback/verificacionPendiente";
        }
    
        return "redirect:/login";
    }    

}
