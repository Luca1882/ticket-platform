package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("newUser", new User()); // Inizializza un oggetto User vuoto
        return "user/create"; // Mostra il file create.html nella cartella user
    }

    // 1. Modifica disponibilità (solo per se stessi)
    @PostMapping("/edit")
    public String updateDisponibilita(Principal principal) {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        user.setDisponibile(!user.isDisponibile());
        userService.saveUser(user);

        return "redirect:/user/" + user.getId(); // Ritorna alla pagina dettaglio
    }

    // 2. Creazione nuovo operatore (solo per admin)
    @PostMapping("/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        // Validazioni base
        if (username == null || username.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Username obbligatorio");
            return "redirect:/user/create";
        }

        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Email obbligatoria");
            return "redirect:/user/create";
        }

        if (userService.getUserByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email già in uso");
            return "redirect:/user/create";
        }

        // Creazione utente
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // usa la password inserita
        newUser.setDisponibile(true);

        userService.saveUser(newUser);
        redirectAttributes.addFlashAttribute("successMessage", "Operatore creato con successo");

        return "redirect:/admin/dashboard" + newUser.getId();
    }
}