package com.crm.controllers;

import com.crm.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Affiche la page de connexion
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        // Appel du service pour tenter de se connecter
        ResponseEntity<String> response = authService.login(email, password);

        // Vérification si la connexion est réussie (status code 200)
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("user", email);
            model.addAttribute("token", response.getBody());  // Stocke le token pour l'utiliser
            return "success";  // Si la connexion est réussie, redirige vers la page 'success'
        }
        // Si la réponse est 401 Unauthorized, cela signifie que les identifiants sont incorrects
        else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            model.addAttribute("error", "Identifiants incorrects");  // Ajoute un message d'erreur
            return "login";  // Redirige vers la page de connexion avec le message d'erreur
        }
        // Dans les autres cas, redirige vers une page d'erreur générique
        else {
            model.addAttribute("error", "Une erreur est survenue, veuillez réessayer.");  // Message d'erreur générique
            return "login";  // Redirige vers la page de connexion
        }
    }

    @GetMapping("/logout")
    public String logout(@RequestParam String token, Model model) {
        // Appel du service pour effectuer la déconnexion
        authService.logout(token);
        model.addAttribute("message", "Déconnexion réussie");  // Ajoute un message de déconnexion réussie
        return "login";  // Redirige vers la page de connexion après la déconnexion
    }
}
