package com.alonso.eatelligence.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alonso.eatelligence.email.EmailService;
import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.IAlergenoService;
import com.alonso.eatelligence.service.IPedidoService;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.service.IRecruitmentService;
import com.alonso.eatelligence.service.IUsuarioService;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAlergenoService alergenoService;

    @Autowired
    private IPlatoService platoService;

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRecruitmentService recruitmentService;


    @ModelAttribute
    public void addDashboardStats(
        Model model,
        @SessionAttribute("restaurante") Restaurante restaurante
    ) {
        long totalPlatos = this.platoService.countByRestaurante(restaurante);

        long totalCocineros = this.usuarioService
        .countByRestauranteAsignadoAndRol(restaurante, NombreRol.COCINERO);

        long totalRepartidores = this.usuarioService
        .countByRestauranteAsignadoAndRol(restaurante, NombreRol.REPARTIDOR);

        long pedidosHoy = this.pedidoService.getPedidosHoy(restaurante);

        model.addAttribute("totalPlatos", totalPlatos);
        model.addAttribute("totalCocineros", totalCocineros);
        model.addAttribute("totalRepartidores", totalRepartidores);
        model.addAttribute("pedidosHoy", pedidosHoy);
    }

    @GetMapping({"", "/", "/dashboard"})
    public String goAdmin() {
        return "admin/dashboard";
    }

    @GetMapping("/charts")
    public String goCharts() {
        return "admin/charts";
    }

    @GetMapping("/tables")
    public String goTables() {
        return "admin/tables";
    }

    @ModelAttribute("nuevoPlato")
    public PlatoDTO registroPlato() {
        return new PlatoDTO();
    }

    @GetMapping("/plates")
    public String goPlates(Model model) {
        model.addAttribute("platos", this.platoService.getAll());
        model.addAttribute("alergenosDisponibles", this.alergenoService.getAll());
        return "admin/platos";
    }

    @PostMapping("/plates/add")
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
            return "admin/platos";
        }

        this.platoService.saveFromDTO(nuevoPlato, restaurante);
        return "redirect:/admin/plates";
    }

    @ModelAttribute("nuevoEmpleado")
    public EmpleadoDTO registroEmpleado() {
        return new EmpleadoDTO();
    }

    @PostMapping("/recruit")
    public String reclutarEmpleado(
        @SessionAttribute("restaurante") Restaurante restaurante,
        @Valid @ModelAttribute("nuevoEmpleado") EmpleadoDTO nuevoEmpleado,
        BindingResult result,
        Model model,
        RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, nuevoEmpleado.getClass())
                .ifPresent(error -> model.addAttribute("error", error.getDefaultMessage()));
            return "admin/dashboard";
        }

        try {
            NombreRol.valueOf(nuevoEmpleado.getRol());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Debes seleccionar un rol válido");
            return "admin/dashboard";
        }

        Optional<Usuario> u = this.usuarioService.findByUsernameAndEmail(nuevoEmpleado.getUsername(), nuevoEmpleado.getEmail());

        if (u.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "admin/dashboard";
        }

        if (!u.get().isVerificado()) {
            model.addAttribute("error", "Pide al usuario que verifique su cuenta antes de invitarlo.");
            return "admin/dashboard";
        }

        if (u.get().getRestauranteAsignado() != null) {
            model.addAttribute("error", "El usuario ya tiene un restaurante asignado.");
            return "admin/dashboard";
        }

        if (this.recruitmentService.findValidTokenByUsername(u.get().getUsername()) != null) {
            model.addAttribute("error", "El usuario ya tiene una invitación pendiente.");
            return "admin/dashboard";
        }

        RecruitmentToken expiredToken = this.recruitmentService.findTokenByUsername(u.get().getUsername());
        if (expiredToken != null && expiredToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            this.recruitmentService.delete(expiredToken);
        }

        RecruitmentToken rt = this.recruitmentService.save(this.recruitmentService.create(nuevoEmpleado, restaurante));

        try {
            this.emailService.sendEmail(
                nuevoEmpleado.getEmail(),
                "Eatelligence - " + restaurante.getNombreComercial() + " te invita a unirte a su equipo como " + nuevoEmpleado.getRol().toLowerCase(),
                "reclutamiento",
                Map.of(
                    "username", nuevoEmpleado.getUsername(),
                    "restaurantName", restaurante.getNombreComercial(),
                    "role", nuevoEmpleado.getRol().toLowerCase(),
                    "invitationUrl", "http://localhost:8080/recruit?token=" + rt.getToken()
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error al enviar el correo de verificación. Inténtalo más tarde.");
            return "admin/dashboard";
        }

        ra.addFlashAttribute("success", "Invitación enviada correctamente a " + nuevoEmpleado.getEmail() + ".");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/cocineros")
    public String goCocineros(
        @SessionAttribute("restaurante") Restaurante restaurante,
        Model model
    ) {
        model.addAttribute("cocineros", this.usuarioService.findAllByRestauranteAsignadoAndRol(
            restaurante,
            NombreRol.COCINERO
        ));
        return "admin/cocineros";
    }

    @GetMapping("/repartidores")
    public String goRepartidores(
        @SessionAttribute("restaurante") Restaurante restaurante,
        Model model
    ) {
        model.addAttribute("repartidores", this.usuarioService.findAllByRestauranteAsignadoAndRol(
            restaurante,
            NombreRol.REPARTIDOR
        ));
        return "admin/repartidores";
    }
}