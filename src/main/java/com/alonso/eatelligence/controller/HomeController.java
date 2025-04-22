package com.alonso.eatelligence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @GetMapping("/")
    public String goIndex() {
        return "index";
    }
    
    @GetMapping({"/admin", "/admin/dashboard"})
    public String goAdmin() {
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
