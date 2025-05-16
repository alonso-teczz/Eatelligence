package com.alonso.eatelligence.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.IDireccionService;
import com.alonso.eatelligence.service.IRestauranteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DireccionController {

    @Autowired
    private IDireccionService direccionService;

    @Autowired
    private IRestauranteService restauranteService;

    @PostMapping("/set-shipping-address")
    public String establecerDireccionEnvio(
        @RequestParam("direccionId") String direccionId,
        Model model,
        HttpSession session,
        RedirectAttributes ra
    ) {
        switch (direccionId) {
            case "NONE":
                session.setAttribute("direccionEnvioId", null);
                ra.addFlashAttribute("direccionOmitida", true);
                break;

            case "REST":
                Optional<Restaurante> optRest = this.restauranteService
                    .findByUsuario((Usuario) session.getAttribute("usuario"));

                if (optRest.isPresent() && optRest.get().getDireccion() != null) {
                    session.setAttribute("direccionEnvioId", optRest.get().getDireccion().getId());
                } else {
                    ra.addFlashAttribute("errorDireccion", "No se encontró la dirección del restaurante.");
                }

                break;

            default:
                try {
                    Long id = Long.parseLong(direccionId);
                    Optional<Direccion> direccionOpt = this.direccionService.getById(id);

                    if (direccionOpt.isPresent()) {
                        session.setAttribute("direccionEnvioId", id);
                    } else {
                        ra.addFlashAttribute("errorDireccion", "La dirección seleccionada no existe.");
                    }
                } catch (NumberFormatException e) {
                    ra.addFlashAttribute("errorDireccion", "Dirección seleccionada no válida.");
                }

                break;
        }

        return "redirect:/";
    }

}
