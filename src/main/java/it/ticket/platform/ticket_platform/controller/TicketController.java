package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.ticket.platform.ticket_platform.enumeration.Status;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.CategoryService;
import it.ticket.platform.ticket_platform.service.NoteService;
import it.ticket.platform.ticket_platform.service.TicketService;
import it.ticket.platform.ticket_platform.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    // Inietto le dipendenze dei servizi

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private CategoryService categoryService;

    // Dashboard Admin
    @GetMapping("/admin/dashboard")
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

    // METODI CRUD TICKET
    // Crea un ticket

    @GetMapping("/ticket/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("categorie", categoryService.getAllCategories());
        return "ticket/create";
    }

    @PostMapping("/ticket/create")
    public String createTicket(@Valid @ModelAttribute("ticket") Ticket ticket,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            return "ticket/create";
        }

        User operatoreDisponibile = userService.findOperatoreDisponibile();
        if (operatoreDisponibile == null) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            model.addAttribute("error", "Nessun operatore disponibile al momento.");
            return "ticket/create";
        }

        ticket.setUser(operatoreDisponibile);
        ticket.setDataCreazione(LocalDateTime.now());
        ticket.setStatus(Status.IN_LAVORAZIONE);

        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("success", "Ticket creato con successo!");
        return "redirect:/ticket";
    }

    // Modifica del ticket
    @GetMapping("/ticket/edit/{id}")
    public String editTicket(@PathVariable("id") Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoryService.getAllCategories());
        return "ticket/edit";
    }

    // Post modifica ticket
    @PostMapping("/ticket/edit/{id}")
    public String updateTicket(@PathVariable("id") Long id,
            @Valid @ModelAttribute("ticket") Ticket ticket,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            return "ticket/edit";
        }
        ticketService.aggiornaTicket(id, ticket);
        redirectAttributes.addFlashAttribute("success", "Ticket aggiornato con successo!");
        return "redirect:/ticket";
    }

    // Eliminazion del ticket
    @PostMapping("/ticket/delete/{id}")
    public String deleteTicket(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,
            Principal principal) {
        Optional<User> optionalUser = userService.getUserByEmail(principal.getName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Recupera il ticket prima di cancellare la nota associata
            Ticket ticket = ticketService.getTicketById(id);

            // Cancellazione della nota associata al ticket
            noteService.deleteByTicket(ticket);

            // Controllo se l'utente è un admin
            if (user.getRole().getName().equals("ROLE_ADMIN")) {
                ticket.setStatus(Status.IN_ATTESA);
                ticketService.saveTicket(ticket);

                redirectAttributes.addFlashAttribute("success", "Ticket impostato come 'IN_ATTESA'.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Non hai i permessi per cancellare il ticket.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Utente non trovato.");
        }

        return "redirect:/ticket";
    }
}
