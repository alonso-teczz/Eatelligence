package com.alonso.eatelligence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavController {

    @GetMapping("")
    public String goIndex() {
        return "index";
    }
    
}
