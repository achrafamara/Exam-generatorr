package dev.danvega.controller;


import dev.danvega.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showSignupPage() {
        return "signup"; // Retourne signup.html
    }

    @PostMapping
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        boolean isRegistered = userService.registerUser(username, password);
        if (isRegistered) {
            return "redirect:/login?signupSuccess=true"; // Redirige vers login avec succès
        } else {
            model.addAttribute("error", "Username already exists!");
            return "signup";
        }
    }
}

