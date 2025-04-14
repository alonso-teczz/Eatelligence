package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.service.impl.ResturanteServiceImp;
import com.alonso.eatelligence.service.impl.UsuarioServiceImp;
import com.alonso.eatelligence.validation.groups.ValidacionCliente;
import com.alonso.eatelligence.validation.groups.ValidacionRestaurante;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private ResturanteServiceImp restauranteService;

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
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "registerUser";
        }

        if (this.usuarioService.existsByUsername(registroDTO.getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "registerUser";
        }

        try {
            this.usuarioService.save(this.usuarioService.clientDTOtoEntity(registroDTO));
        } catch (DataIntegrityViolationException e) {
            System.out.println("Error al guardar el usuario: " + e.getMessage());
            result.rejectValue("username", "error.username", "Este nombre de usuario ya existe");
            return "registerUser";
        }

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

        if (this.usuarioService.existsByUsername(registroDTO.getPropietario().getUsername())) {
            result.rejectValue("username", "error.username", "El nombre de usuario ya está en uso");
            return "registerUser";
        }

        try {
            this.restauranteService.save(this.restauranteService.restDTOtoEntity(registroDTO));
        } catch (DataIntegrityViolationException e) {
            System.out.println("Error al guardar el usuario: " + e.getMessage());
            result.rejectValue("username", "error.username", "Este nombre de usuario ya existe");
            return "registerUser";
        }

        return "redirect:/";
    }

    @GetMapping("/api/users/exists")
    @ResponseBody
    public boolean comprobarUsername(@RequestParam String username) {
        return this.usuarioService.existsByUsername(username);
    }

}
