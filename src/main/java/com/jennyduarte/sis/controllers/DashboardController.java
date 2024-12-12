package com.jennyduarte.sis.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping({"/dashboard", "/"})
    public String mostrarDashboard(Model model) {
        model.addAttribute("mensaje", "Bienvenido al Dashboard");
        return "dashboard"; // Vista dashboard.html
    }
}
