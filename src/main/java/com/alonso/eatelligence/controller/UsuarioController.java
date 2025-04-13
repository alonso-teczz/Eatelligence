package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.service.impl.UsuarioServiceImpl;
import com.alonso.eatelligence.validation.groups.ValidacionCliente;
import com.alonso.eatelligence.validation.groups.ValidacionRestaurante;

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
        @Validated(ValidacionCliente.class) @ModelAttribute("registroUsuario") ClienteRegistroDTO registroDTO,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        if (this.usuarioService.existsByUsername(registroDTO.getNombre())) {
            result.rejectValue("username", "error.username", "El usuario ya existe");
            return "registerUser";
        }

        this.usuarioService.save(this.usuarioService.clientDTOtoEntity(registroDTO));

        return "redirect:/";
    }

    @PostMapping("/validRestaurantReg")
    public String validateRestaurantRegister(
        @Validated(ValidacionRestaurante.class) @ModelAttribute("registroRestaurante") RestauranteRegistroDTO registroDTO,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        //this.restauranteService.insert(this.restauranteService.restDTOtoEntity(registroDTO));

        return "redirect:/";
    }
}
