package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.ticket.platform.ticket_platform.model.Note;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.NoteService;
import it.ticket.platform.ticket_platform.service.TicketService;
import it.ticket.platform.ticket_platform.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    // Mostra il form per aggiungere una nota
    @GetMapping("/ticket/{ticketId}/add")
    public String showNoteForm(@PathVariable Long ticketId, Model model, Principal principal,
                                RedirectAttributes redirectAttributes) {

        Ticket ticket = ticketService.getTicketById(ticketId);
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Permessi: admin o operatore assegnato
        if (!user.getRole().getName().equals("ROLE_ADMIN") &&
            !ticket.getUser().getId().equals(user.getId())) {

            redirectAttributes.addFlashAttribute("error", "Non si può aggiungere note a questo ticket.");
            return "redirect:/ticket";
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("nota", new Note());
        return "ticket/note_form";
    }

    // Salva la nota
    @PostMapping("/ticket/{ticketId}/add")
    public String saveNote(@PathVariable Long ticketId,
                           @Valid @ModelAttribute("nota") Note nota,
                           BindingResult bindingResult,
                           Model model,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {

        Ticket ticket = ticketService.getTicketById(ticketId);
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (!user.getRole().getName().equals("ROLE_ADMIN") &&
            !ticket.getUser().getId().equals(user.getId())) {

            redirectAttributes.addFlashAttribute("error", "Non si può salvare note su questo ticket.");
            return "redirect:/ticket";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("ticket", ticket);
            return "ticket/note_form";
        }

        noteService.addNote(nota, ticketId, user.getEmail());
        redirectAttributes.addFlashAttribute("success", "Nota salvata con successo.");
        return "redirect:/ticket";
    }
}