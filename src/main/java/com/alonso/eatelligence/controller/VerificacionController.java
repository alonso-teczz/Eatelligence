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

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;
import com.alonso.eatelligence.service.EmailService;
import com.alonso.eatelligence.service.imp.ResturanteServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.service.imp.VerificationTokenServiceImp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VerificacionController {

    private final VerificationTokenServiceImp tokenService;
    private final UsuarioServiceImp usuarioService;
    private final ResturanteServiceImp restauranteService;
    private final EmailService emailService;

    @GetMapping("/verificar")
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

    @PostMapping("/reenviar-verificacion")
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
                    " para solicitar otro correo de verificación.");
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
                    " para solicitar otro correo de verificación.");
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
    
                    this.emailService.sendVerificationEmail(
                        u.getEmail(),
                        "Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", u.getNombre(),
                            "urlVerificacion", "http://localhost:8080/verificar?token=" + newVT.getToken(),
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
    
                    this.emailService.sendVerificationEmail(
                        r.getEmailEmpresa(),
                        "Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", r.getNombreComercial(),
                            "urlVerificacion", "http://localhost:8080/verificar?token=" + newVT.getToken(),
                            "proximoIntento", minutosEspera * 2 + " minutos"
                        )
                    );
                }
    
                default -> {
                    return ResponseEntity.badRequest().body("Tipo de token inválido");
                }
            }
    
            return ResponseEntity.ok("Correo de verificación reenviado correctamente. Podrás solicitar otro en " + (minutosEspera * 2) + " minutos.");
    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al reenviar el correo: " + e.getMessage());
        }
    }    
}
