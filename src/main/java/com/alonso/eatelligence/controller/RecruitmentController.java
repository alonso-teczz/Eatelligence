package com.alonso.eatelligence.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.service.IRecruitmentService;
import com.alonso.eatelligence.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecruitmentController {

    private final IRecruitmentService recruitmentService;
    private final IUsuarioService usuarioService;

    @GetMapping("/recruit")
    public String acceptInvitation(
        @RequestParam("token") String token,
        Model model
    ) {
        Optional<RecruitmentToken> opt = this.recruitmentService.findByToken(token);

        if (opt.isEmpty()) {
            model.addAttribute("error", "Token inválido.");
            return "feedback/reclutamientoFallido";
        }

        RecruitmentToken rt = opt.get();

        if (rt.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token expirado.");
            return "feedback/reclutamientoFallido";
        }

        this.usuarioService.addRoleToUser(rt.getUsername(), rt.getRol());
        this.usuarioService.asignarRestaurante(this.usuarioService.findByUsername(rt.getUsername()).get(), rt.getRestaurante());
        this.usuarioService.findByUsername(rt.getUsername()).get().setFechaReclutamiento(LocalDateTime.now());
        this.recruitmentService.delete(rt);

        model.addAttribute("nombreRestaurante", rt.getRestaurante().getNombreComercial());
        model.addAttribute("rol", rt.getRol().name());
        return "feedback/reclutamientoExitoso";
    }
}