package com.alonso.eatelligence.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    "opcionesMenu"
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

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
        Model model,
        HttpServletRequest request
    ) {
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, formLogin.getClass())
                .ifPresent(error -> model.addAttribute("globalError", error.getDefaultMessage()));
    
            return "login";
        }
        
        Optional<Usuario> optUser = this.usuarioService.findByUsername(formLogin.getUsername());

        if (optUser.isEmpty() || !this.usuarioService.checkPassword(optUser.get(), formLogin.getPassword())) {
            model.addAttribute("globalError", "Usuario o contraseña incorrectos");
            return "login";
        }

        Usuario u = optUser.get();

        Optional<Restaurante> r = this.restauranteService.findByUsuario(u);

        if (r.isPresent()) {
            model.addAttribute("restaurante", r.get());
        } else {
            model.addAttribute("restaurante", null);
        }
        
        List<OpcionMenu> opciones = this.menuService.obtenerOpcionesParaUsuario(u.getUsername());
        Map<String, List<OpcionMenu>> menuPorSeccion = opciones.stream()
            .collect(Collectors.groupingBy(OpcionMenu::getSeccion, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("opcionesMenu", menuPorSeccion);
        model.addAttribute("usuario", u);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            u,
            null,
            u.getRoles().stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre().name()))
                .toList()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        request.getSession().setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            context
        );

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(SessionStatus session, HttpServletRequest request, RedirectAttributes ra) {
        session.setComplete();
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        ra.addFlashAttribute("logoutSuccess", true);
        return "redirect:/login";
    }

}