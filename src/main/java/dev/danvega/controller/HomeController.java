package dev.danvega.controller;

import dev.danvega.model.TestHistory;
import dev.danvega.model.User;
import dev.danvega.service.TestHistoryService;
import dev.danvega.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    TestHistoryService testHistoryService;
    @GetMapping("/")
    public String home(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Nom de l'utilisateur
            model.addAttribute("username", username);
        } else {
            model.addAttribute("username", "Utilisateur inconnu");
        }

        return "home"; // Correspond à home.html
    }


}
