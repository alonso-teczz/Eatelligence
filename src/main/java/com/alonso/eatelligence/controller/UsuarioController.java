package com.alonso.eatelligence.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;
import com.alonso.eatelligence.service.EmailService;
import com.alonso.eatelligence.service.imp.ResturanteServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.service.imp.VerificationTokenServiceImp;
import com.alonso.eatelligence.validation.groups.ValidacionCliente;
import com.alonso.eatelligence.validation.groups.ValidacionRestaurante;

import jakarta.mail.MessagingException;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private ResturanteServiceImp restauranteService;

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

    @GetMapping("/regUser")
    public String mostrarFormularioRegistro() {
        return "registerUser";
    }

    @PostMapping("/validClientReg")
    public String validateClientRegister(
        @Validated(ValidacionCliente.class) @ModelAttribute("registroUsuario") ClienteRegistroDTO registroDTO,
        BindingResult result,
        RedirectAttributes ra
    ) {
        if (this.usuarioService.existsByUsername(registroDTO.getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "registerUser";
        }

        if (result.hasErrors()) {
            return "registerUser";
        }

        Usuario u = this.usuarioService.save(this.usuarioService.clientDTOtoEntity(registroDTO));
        VerificationToken vt = this.tokenService.save(this.tokenService.forUser(u));
        
        try {
            this.emailService.sendVerificationEmail(
                u.getEmail(),
                "Verificar cuenta",
                "verificacion",
                Map.of(
                    "nombre", u.getNombre(),
                    "urlVerificacion", "http://localhost:8080/verify?token=" + vt.getToken()
                )
            );
        
            ra.addFlashAttribute("correoFallido", false);
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            ra.addFlashAttribute("correoFallido", true);
        }
        
        ra.addFlashAttribute("token", vt.getToken());
        ra.addFlashAttribute("email", u.getEmail());
        
        return "redirect:/registroExitoso";
        
    }

    @PostMapping("/validRestaurantReg")
    public String validateRestaurantRegister(
        @Validated(ValidacionRestaurante.class) @ModelAttribute("registroRestaurante") RestauranteRegistroDTO registroDTO,
        BindingResult result,
        RedirectAttributes ra
    ) {
        if (this.usuarioService.existsByUsername(registroDTO.getPropietario().getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "registerUser";
        }

        if (result.hasErrors()) {
            return "registerUser";
        }

        Restaurante r = this.restauranteService.save(this.restauranteService.restDTOtoEntity(registroDTO));
        VerificationToken vt = this.tokenService.save(this.tokenService.forRestaurant(r));
        
        boolean correoRestauranteFallido = false;
        boolean correoPropietarioFallido = false;
        
        try {
            this.emailService.sendVerificationEmail(
                r.getEmailEmpresa(),
                "Verificar cuenta del restaurante",
                "verificacion",
                Map.of(
                    "nombre", r.getNombreComercial(),
                    "urlVerificacion", "http://localhost:8080/verify?token=" + vt.getToken()
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.err.println("Error al enviar el correo a la empresa: " + e.getMessage());
            correoRestauranteFallido = true;
        }
        
        try {
            this.emailService.sendVerificationEmail(
                r.getPropietario().getEmail(),
                "Verificar cuenta de usuario propietario",
                "verificacion",
                Map.of(
                    "nombre", r.getPropietario().getNombre(),
                    "urlVerificacion", "http://localhost:8080/verify?token=" + vt.getToken()
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.err.println("Error al enviar el correo al propietario: " + e.getMessage());
            correoPropietarioFallido = true;
        }
        
        ra.addFlashAttribute("token", vt.getToken());
        ra.addFlashAttribute("email", r.getPropietario().getEmail());
        ra.addFlashAttribute("nombreRestaurante", r.getNombreComercial());
        ra.addFlashAttribute("correoEmpresaFallido", correoRestauranteFallido);
        ra.addFlashAttribute("correoPropietarioFallido", correoPropietarioFallido);
        
        return "redirect:/registroExitoso";
    }

    @GetMapping("/api/users/exists")
    @ResponseBody
    public boolean comprobarUsername(@RequestParam String username) {
        return this.usuarioService.existsByUsername(username);
    }

    @GetMapping("/registroExitoso")
    public String mostrarRegistroExitoso() {
        return "registroExitoso";
    }

}
