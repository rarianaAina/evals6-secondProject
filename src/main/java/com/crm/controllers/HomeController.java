package com.crm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        //model.addAttribute("message", "Bienvenue sur la page d'accueil !");
        return "login";
    }
}

