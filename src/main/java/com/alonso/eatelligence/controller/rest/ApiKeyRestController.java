package com.alonso.eatelligence.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/apikeys")
public class ApiKeyRestController {

    @Value("${geoapify.api.key}")
    private String geoapifyKey;

    @GetMapping("/geoapify")
    public Map<String, String> getApiKey() {
        return Map.of("apiKey", geoapifyKey);
    }
}
