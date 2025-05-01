package com.alonso.eatelligence.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.service.IDireccionService;

@Controller
@SessionAttributes({
    "coordenadasEnvio"
})
public class DireccionController {

    @Autowired
    private IDireccionService direccionService;

    @PostMapping("/set-shipping-address")
    public String establecerDireccionEnvio(
        @RequestParam("direccionId") Long direccionId,
        Model model,
        RedirectAttributes ra
    ) {
        Direccion direccion = direccionService.getById(direccionId).orElse(null);

        if (direccion == null) {
            ra.addFlashAttribute("errorDireccion", "No se encontró la dirección seleccionada.");
            return "redirect:/";
        }

        model.addAttribute("coordenadasEnvio", Map.of(
            "lat", direccion.getLatitud(),
            "lon", direccion.getLongitud()
        ));

        return "redirect:/";
    }
}
