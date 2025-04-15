package com.alonso.eatelligence.controller;

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
public class VerificationController {

    private final VerificationTokenServiceImp tokenService;
    private final UsuarioServiceImp usuarioService;
    private final ResturanteServiceImp restauranteService;
    private final EmailService emailService;

    @GetMapping("/verify")
    @Transactional
    public String verificar(@RequestParam("token") String token, Model model) {
        VerificationToken vt = this.tokenService.findByToken(token).orElse(null);

        if (vt == null || vt.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "El token es inválido o ha expirado.");
            return "verificacion-fallida";
        }

        switch (vt.getTipo()) {
            case USUARIO: 
                Usuario usuario = vt.getUsuario();
                usuario.setVerificado(true);
                this.usuarioService.save(usuario);
                model.addAttribute("nombre", usuario.getNombre());
                break;
            case RESTAURANTE:
                Restaurante restaurante = vt.getRestaurante();
                restaurante.setVerificado(true);
                this.restauranteService.save(restaurante);
                model.addAttribute("nombre", restaurante.getNombreComercial());
                break;
        }

        this.tokenService.delete(vt);
        return "verificacion-exitosa";
    }

    @PostMapping("/reenviar-verificacion")
    @ResponseBody
    public ResponseEntity<?> reenviarVerificacion(
        @RequestParam String token,
        @RequestParam String tipo
    ) {
        Optional<VerificationToken> optToken = tokenService.findByToken(token);
        if (optToken.isEmpty()) return ResponseEntity.badRequest().body("Token inválido");
    
        VerificationToken oldToken = optToken.get();
        if (oldToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokenService.delete(oldToken);
            return ResponseEntity.status(410).body("El token ha expirado");
        }
    
        try {
            switch (tipo.toUpperCase()) {
                case "USUARIO" -> {
                    Usuario u = oldToken.getUsuario();
                    if (u == null) return ResponseEntity.badRequest().body("Token no asociado a un usuario");
                    tokenService.delete(oldToken);
    
                    VerificationToken nuevo = tokenService.save(tokenService.forUser(u));
                    emailService.sendVerificationEmail(
                        u.getEmail(),
                        "Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", u.getNombre(),
                            "urlVerificacion", "http://localhost:8080/verify?token=" + nuevo.getToken()
                        )
                    );
                }
    
                case "RESTAURANTE" -> {
                    Restaurante r = oldToken.getRestaurante();
                    if (r == null) return ResponseEntity.badRequest().body("Token no asociado a un restaurante");
                    tokenService.delete(oldToken);
    
                    VerificationToken nuevo = tokenService.save(tokenService.forRestaurant(r));
                    emailService.sendVerificationEmail(
                        r.getEmailEmpresa(),
                        "Reenvío de verificación",
                        "verificacion",
                        Map.of(
                            "nombre", r.getNombreComercial(),
                            "urlVerificacion", "http://localhost:8080/verify?token=" + nuevo.getToken()
                        )
                    );
                }
    
                default -> {
                    return ResponseEntity.badRequest().body("Tipo inválido");
                }
            }
    
            return ResponseEntity.ok().build();
    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al reenviar el correo: " + e.getMessage());
        }
    }    
}
