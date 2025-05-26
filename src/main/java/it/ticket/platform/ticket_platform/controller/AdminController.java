package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.ticket.platform.ticket_platform.model.Note;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.TicketService;
import it.ticket.platform.ticket_platform.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    // Dashboard Admin
    @GetMapping("/dashboard")
    public String showAdminDashboard(@RequestParam(name = "keyword", required = false) String search,
            @RequestParam(name = "login", required = false) String login,
            Model model,
            Principal principal) {
        principal.getName();
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getName();

        List<Ticket> tickets;
        if ("ROLE_ADMIN".equals(role)) {
            if (search != null && !search.isEmpty()) {
                tickets = ticketService.findByTitleContainingIgnoreCase(search);
            } else {
                tickets = ticketService.getTicket();
            }
        } else {
             tickets = ticketService.getTicketsByUser(user);
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("search", search);
        model.addAttribute("newNote", new Note());

        if ("true".equals(login)) {
            model.addAttribute("successMessage", "Login effettuato con successo!");
        }

        return "admin/dashboard";
    }}
