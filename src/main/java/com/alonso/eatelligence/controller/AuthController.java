package com.alonso.eatelligence.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alonso.eatelligence.model.dto.LoginDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.imp.ResturanteServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({
    "usuario",
    "restaurante"
})
public class AuthController {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private ResturanteServiceImp restauranteService;

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

        Optional<Restaurante> r = this.restauranteService.findByUsuario(u);

        if (r.isPresent()) {
            model.addAttribute("restaurante", r.get());
        } else {
            model.addAttribute("restaurante", null);
        }
        
        model.addAttribute("usuario", u);
        return "redirect:/";
    }



    @PostMapping("/logout")
    public String logout(SessionStatus session, HttpServletRequest request, RedirectAttributes ra) {
        session.setComplete();
        request.getSession().invalidate();
        ra.addFlashAttribute("logoutSuccess", true);
        return "redirect:/login";
    }

}