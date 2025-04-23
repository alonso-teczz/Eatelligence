package com.alonso.eatelligence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
    @GetMapping("/")
    public String goIndex() {
        return "index";
    }
    
    @GetMapping({"/admin", "/admin/dashboard"})
    public String goAdmin(HttpSession session) {
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

}
