package it.ticket.platform.ticket_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.service.TicketService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketService ticketService;

    // Dashboard Admin
    @GetMapping("/dashboard")
    public String showAdminDashboard(@RequestParam(name = "search", required = false) String search, Model model) {
        List<Ticket> tickets;
        if (search != null && !search.isEmpty()) {
            tickets = ticketService.findTicketByTitle(search);
        } else {
            tickets = ticketService.getTicket();
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("search", search);
        return "admin/dashboard";
    }
}
