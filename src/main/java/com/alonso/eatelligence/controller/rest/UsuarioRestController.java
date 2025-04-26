package com.alonso.eatelligence.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alonso.eatelligence.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsuarioRestController {
    
    private final IUsuarioService usuarioService;

    @GetMapping("/exists")
    @ResponseBody
    public boolean comprobarUsername(@RequestParam String username) {
        return this.usuarioService.existsByUsername(username);
    }
}
