package com.eatelligence.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.eatelligence.model.dto.UsuarioRegistroDTO;
import com.eatelligence.service.impl.UsuarioServiceImpl;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @GetMapping("/regUser")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroUsuario", new UsuarioRegistroDTO());
        return "registerUser";
    }

    @GetMapping("/logUser")
    public String login() {
        return "loginUser";
    }

    @PostMapping("/validUserReg")
    public String validateUserRegister(
        @Valid @ModelAttribute("registroUsuario") UsuarioRegistroDTO registroDTO,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        if (this.usuarioService.existeUsuario(registroDTO.getNombre())) {
            result.rejectValue("username",  "Nombre de usuario en uso");
            return "registerUser";
        }

        return "";
    }
}
