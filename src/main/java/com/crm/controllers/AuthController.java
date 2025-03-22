package com.crm.controllers;

import com.crm.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        ResponseEntity<String> response = authService.login(email, password);

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("user", email);
            model.addAttribute("token", response.getBody()); // Stocker le token
            return "success";
        } else {
            model.addAttribute("error", "Identifiants incorrects");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(@RequestParam String token, Model model) {
        authService.logout(token);
        model.addAttribute("message", "Déconnexion réussie");
        return "login";
    }
}
