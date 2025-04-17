package com.alonso.eatelligence.controller;

import com.alonso.eatelligence.model.dto.LoginDTO;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("usuario")
public class AuthController {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @ModelAttribute("loginDTO")
    public LoginDTO loginDTO() {
        return new LoginDTO();
    }

    @PostMapping("/validate-login")
    public String procesarLogin(
        @Valid @ModelAttribute("loginDTO") LoginDTO formLogin,
        BindingResult result,
        Model model
    ) {
        Usuario u = this.usuarioService.findByUsername(formLogin.getUsername());

        if (!this.usuarioService.checkPassword(u, formLogin.getPassword())) {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
            return "login";
        }

        model.addAttribute("usuario", u);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}