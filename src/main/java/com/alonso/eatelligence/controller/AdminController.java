package com.alonso.eatelligence.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.email.EmailService;
import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.service.IAlergenoService;
import com.alonso.eatelligence.service.IPedidoService;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.service.IRecruitmentService;
import com.alonso.eatelligence.service.IRolService;
import com.alonso.eatelligence.service.IUsuarioService;
import com.alonso.eatelligence.utils.ValidationUtils;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@Controller
// @RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAlergenoService alergenoService;

    @Autowired
    private IRolService rolService;

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

    @GetMapping({"/admin", "/admin/dashboard"})
    public String goAdmin(Model model) {
        // Total de platos
        long totalPlatos = this.platoService.countAll();

        // Nº de cocineros activos
        long totalCocineros = this.rolService.findByNombreConUsuarios(NombreRol.COCINERO)
            .map(r -> r.getUsuarios().size()).orElse(0);

        // Nº de repartidores activos
        long totalRepartidores = this.rolService.findByNombreConUsuarios(NombreRol.REPARTIDOR)
            .map(r -> r.getUsuarios().size()).orElse(0);

        // Pedidos realizados hoy
        LocalDate hoy = LocalDate.now();
        long pedidosHoy = this.pedidoService.countPedidosRealizadosEntre(
            hoy.atStartOfDay(),
            hoy.plusDays(1).atStartOfDay()
        );

        // Añadimos al modelo
        model.addAttribute("totalPlatos", totalPlatos);
        model.addAttribute("totalCocineros", totalCocineros);
        model.addAttribute("totalRepartidores", totalRepartidores);
        model.addAttribute("pedidosHoy", pedidosHoy);

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

    @ModelAttribute("nuevoEmpleado")
    public EmpleadoDTO registroEmpleado() {
        return new EmpleadoDTO();
    }

    @PostMapping("/admin/recruit")
    public String reclutarEmpleado(
        @SessionAttribute("restaurante") Restaurante restaurante,
        @Valid @ModelAttribute("nuevoEmpleado") EmpleadoDTO nuevoEmpleado,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            ValidationUtils.getFirstOrderedErrorFromBindingResult(result, nuevoEmpleado.getClass())
                .ifPresent(error -> model.addAttribute("error", error.getDefaultMessage()));
            return "admin/recruit";
        }

        if (nuevoEmpleado.getRol() != NombreRol.COCINERO && nuevoEmpleado.getRol() != NombreRol.REPARTIDOR) {
            model.addAttribute("error", "Debes seleccionar un rol válido");
            return "admin/recruit";
        }

        Optional<Usuario> u = this.usuarioService.findByUsernameAndEmail(nuevoEmpleado.getUsername(), nuevoEmpleado.getEmail());

        if (u.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "admin/recruit";
        }

        if (!u.get().isVerificado()) {
            model.addAttribute("error", "Pide al usuario que verifique su cuenta antes de invitarlo.");
            return "admin/recruit";
        }

        if (u.get().getRestauranteAsignado() != null) {
            model.addAttribute("error", "El usuario ya está asignado a un restaurante.");
            return "admin/recruit";
        }

        if (this.recruitmentService.findByUsername(u.get().getUsername()) != null) {
            model.addAttribute("error", "El usuario ya tiene una invitación pendiente.");
            return "admin/recruit";
        }

        RecruitmentToken rt = this.recruitmentService.save(this.recruitmentService.createToken(nuevoEmpleado));

        try {
            this.emailService.sendEmail(
                nuevoEmpleado.getEmail(),
                restaurante.getNombreComercial() + " te invita a unirte a su equipo como " + nuevoEmpleado.getRol().name().toLowerCase(),
                "reclutamiento",
                Map.of(
                    "username", nuevoEmpleado.getUsername(),
                    "restaurantName", restaurante.getNombreComercial(),
                    "role", nuevoEmpleado.getRol().name().toLowerCase(),
                    "invitationUrl", "http://localhost:8080/recruit?token=" + rt.getToken()
                )
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error al enviar el correo de verificación. Inténtalo más tarde.");
            return "admin/recruit";
        }

        return "redirect:/admin/recruit";
    }

}