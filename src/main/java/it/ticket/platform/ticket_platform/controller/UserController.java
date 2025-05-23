package it.ticket.platform.ticket_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.UserService;

@Controller
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserService userService;

    // Mostra un utente
    @GetMapping("/show/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/show"; // devi creare il template
    }

    // Modifica utente
    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/edit"; // devi creare il template
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Utente aggiornato con successo.");
        return "redirect:/user/show/" + user.getId();
    }
}
