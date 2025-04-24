package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IAlergenoService;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.validation.Valid;

@Controller
// @RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAlergenoService alergenoService;

    @Autowired
    private IPlatoService platoService;

    @GetMapping({"/admin", "/admin/dashboard"})
    public String goAdmin() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/charts")
    public String goCharts() {
        return "admin/charts";
    }

    @GetMapping("/admin/tables")
    public String goTables() {
        return "admin/tables";
    }

    @ModelAttribute("nuevoPlato")
    public PlatoDTO registroPlato() {
        return new PlatoDTO();
    }

    @GetMapping("/admin/plates")
    public String goPlates(Model model) {
        model.addAttribute("platos", this.platoService.getAll());
        model.addAttribute("alergenosDisponibles", this.alergenoService.getAll());
        return "admin/plates";
    }

    @PostMapping("/admin/plates/add")
    public String guardarPlato(
        @SessionAttribute("restaurante") Restaurante restaurante,
        @Valid @ModelAttribute("nuevoPlato") PlatoDTO nuevoPlato,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("platos", this.platoService.getAll());
            model.addAttribute("alergenosDisponibles", this.alergenoService.getAll());
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, nuevoPlato.getClass())
                .ifPresent(error -> model.addAttribute("globalError", error.getDefaultMessage()));
            return "admin/plates";
        }

        this.platoService.saveFromDTO(nuevoPlato, restaurante);
        return "redirect:/admin/plates";
    }

}