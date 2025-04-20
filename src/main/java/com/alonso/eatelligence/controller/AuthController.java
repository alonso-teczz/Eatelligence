package com.alonso.eatelligence.controller;

import com.alonso.eatelligence.model.dto.LoginDTO;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, formLogin.getClass())
                .ifPresent(error -> model.addAttribute("globalError", error.getDefaultMessage()));
    
            return "login";
        }
        
        Usuario u = this.usuarioService.findByUsername(formLogin.getUsername());

        if (!this.usuarioService.checkPassword(u, formLogin.getPassword())) {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
            return "login";
        }

        model.addAttribute("usuario", u);
        return "redirect:/";
    }



    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("logoutSuccess", true);
        return "redirect:/login";
    }

}