package com.eatelligence.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController {

    @GetMapping("")
    public String entry() {
        return "index";
    }
    
}
