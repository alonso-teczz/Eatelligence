package com.alonso.eatelligence.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.alonso.eatelligence.model.entity.OpcionMenu;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.IMenuService;
import com.alonso.eatelligence.service.IRestauranteService;
import com.alonso.eatelligence.service.IUsuarioService;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({
    "usuario",
    "restaurante",
    "menu"
})
public class AuthController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRestauranteService restauranteService;

    @Autowired
    private IMenuService menuService;

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
        
        List<OpcionMenu> opciones = this.menuService.obtenerOpcionesParaUsuario(u.getUsername());
        Map<String, List<OpcionMenu>> menuPorSeccion = opciones.stream()
            .collect(Collectors.groupingBy(OpcionMenu::getSeccion));

        model.addAttribute("usuario", u);
        model.addAttribute("menu", menuPorSeccion);
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