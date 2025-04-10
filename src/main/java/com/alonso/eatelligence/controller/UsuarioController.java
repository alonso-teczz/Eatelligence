package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.service.impl.UsuarioServiceImpl;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;


    @GetMapping("/regUser")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroUsuario", new ClienteRegistroDTO());
        model.addAttribute("registroRestaurante", new RestauranteRegistroDTO());
        return "registerUser";
    }

    @PostMapping("/validClientReg")
    public String validateClientRegister(
        @Valid @ModelAttribute("registroUsuario") ClienteRegistroDTO registroDTO,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        if (this.usuarioService.existsUser(registroDTO.getNombre())) {
            result.rejectValue("nombre",  "Nombre de usuario en uso");
            return "registerUser";
        }

        this.usuarioService.insert(this.usuarioService.ClientDTOtoEntity(registroDTO));

        return "";
    }

    @PostMapping("/validRestaurantReg")
    public String validateRestaurantRegister(
        @Valid @ModelAttribute("registroRestaurante") RestauranteRegistroDTO registroDTO,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        // this.usuarioService.insert(this.usuarioService.ClientDTOtoEntity(registroDTO));

        return "";
    }
}
