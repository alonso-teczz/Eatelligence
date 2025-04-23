package com.alonso.eatelligence.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.alonso.eatelligence.email.EmailService;
import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;
import com.alonso.eatelligence.service.imp.RestauranteServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.service.imp.VerificationTokenServiceImp;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private RestauranteServiceImp restauranteService;

    @Autowired
    private VerificationTokenServiceImp tokenService;
    
    @Autowired
    private EmailService emailService;

    @ModelAttribute("registroUsuario")
    public ClienteRegistroDTO registroUsuario() {
        return new ClienteRegistroDTO();
    }

    @ModelAttribute("registroRestaurante")
    public RestauranteRegistroDTO registroRestaurante() {
        return new RestauranteRegistroDTO();
    }
    
    @GetMapping("/register")
    public String mostrarFormularioRegistro() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        return "register";
    }

    @GetMapping("/api/users/exists")
    @ResponseBody
    public boolean comprobarUsername(@RequestParam String username) {
        return this.usuarioService.existsByUsername(username);
    }
    
    @PostMapping("/validate-client-reg")
    public String validateClientRegister(
        @Valid @ModelAttribute("registroUsuario") ClienteRegistroDTO formCliente,
        BindingResult result,
        RedirectAttributes ra,
        Model model
    ) {
        if (this.usuarioService.existsByUsername(formCliente.getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "register";
        }
    
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, formCliente.getClass())
                .ifPresent(error -> model.addAttribute("globalError", error.getDefaultMessage()));

            return "register";
        }
    
        Usuario u = this.usuarioService.save(this.usuarioService.clientDTOtoEntity(formCliente));
        VerificationToken vt = this.tokenService.save(this.tokenService.forUser(u, 0));
        
        boolean correoUsuarioFallido = false;
        
        try {
            this.emailService.sendVerificationEmail(
                u.getEmail(),
                "Verificar cuenta",
                "verificacion",
                Map.of(
                    "nombre", u.getNombre(),
                    "urlVerificacion", "http://localhost:8080/verificar?token=" + vt.getToken(),
                    "proximoIntento", this.tokenService.formatearTiempoEspera(this.tokenService.calcularTiempoBloqueoSegundos(vt.getIntentosReenvio()))
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            correoUsuarioFallido = true;
        }
        
        ra.addFlashAttribute("tiempoBloqueoEnSegundos", this.tokenService.calcularTiempoBloqueoSegundos(vt.getIntentosReenvio()));
        ra.addFlashAttribute("token", vt.getToken());
        ra.addFlashAttribute("email", u.getEmail());
        ra.addFlashAttribute("correoUsuarioFallido", correoUsuarioFallido);

        return "redirect:/registro-exitoso";
    }    

    @PostMapping("/validate-rest-reg")
    public String validateRestaurantRegister(
        @Valid @ModelAttribute("registroRestaurante") RestauranteRegistroDTO formRestaurante,
        BindingResult result,
        Model model,
        RedirectAttributes ra
    ) {
        if (this.usuarioService.existsByUsername(formRestaurante.getPropietario().getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "register";
        }
    
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, formRestaurante.getClass())
                .ifPresent(error -> model.addAttribute("globalError", error.getDefaultMessage()));
    
            return "login";
        }
    
        Restaurante r = this.restauranteService.save(restauranteService.restDTOtoEntity(formRestaurante));
    
        VerificationToken usuarioVT = this.tokenService.save(this.tokenService.forUser(r.getPropietario(), 0));
        VerificationToken restauranteVT = this.tokenService.save(this.tokenService.forRestaurant(r, 0));
    
        boolean correoUsuarioFallido = false;
        boolean correoRestauranteFallido = false;
    
        try {
            this.emailService.sendVerificationEmail(
                r.getPropietario().getEmail(),
                "Verificar cuenta de usuario del propietario",
                "verificacion",
                Map.of(
                    "nombre", r.getPropietario().getNombre(),
                    "urlVerificacion", "http://localhost:8080/verificar?token=" + usuarioVT.getToken(),
                    "proximoIntento", this.tokenService.formatearTiempoEspera(this.tokenService.calcularTiempoBloqueoSegundos(usuarioVT.getIntentosReenvio()))
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            correoUsuarioFallido = true;
        }
    
        try {
            this.emailService.sendVerificationEmail(
                r.getEmailEmpresa(),
                "Verificar cuenta del restaurante",
                "verificacion",
                Map.of(
                    "nombre", r.getNombreComercial(),
                    "urlVerificacion", "http://localhost:8080/verificar?token=" + restauranteVT.getToken(),
                    "proximoIntento", this.tokenService.formatearTiempoEspera(this.tokenService.calcularTiempoBloqueoSegundos(restauranteVT.getIntentosReenvio()))
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            correoRestauranteFallido = true;
        }
    
        ra.addFlashAttribute("tiempoBloqueoEnSegundos", this.tokenService.calcularTiempoBloqueoSegundos(usuarioVT.getIntentosReenvio()));
        ra.addFlashAttribute("token", usuarioVT.getToken());
        ra.addFlashAttribute("email", r.getPropietario().getEmail());
        ra.addFlashAttribute("nombreRestaurante", r.getNombreComercial());
        ra.addFlashAttribute("correoUsuarioFallido", correoUsuarioFallido);
        ra.addFlashAttribute("correoRestFallido", correoRestauranteFallido);
    
        return "redirect:/registro-exitoso";
    }

    @GetMapping("/registro-exitoso")
    public String mostrarRegistroExitoso(HttpServletRequest request) {
        Map<String, ?> flash = RequestContextUtils.getInputFlashMap(request);

        if (flash == null || !flash.containsKey("token")) {
            return "redirect:/register";
        }

        return "feedback/registroExitoso";
    }

    @GetMapping("/acceso-denegado")
    public String mostrarAccesoDenegado() {
        return "error/access-denied";
    }
    
}
