package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.ticket.platform.ticket_platform.model.Note;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.service.NoteService;
import it.ticket.platform.ticket_platform.service.TicketService;
import it.ticket.platform.ticket_platform.service.UserService;

@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping("/create/{ticketId}")
    public String showCreateNoteForm(@PathVariable Long ticketId, Model model) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        model.addAttribute("ticket", ticket);
        model.addAttribute("newNote", new Note());
        return "note/create"; // Assicurati che esista il template HTML corrispondente
    }

    @PostMapping("/create/{ticketId}")
    public String createNote(@PathVariable Long ticketId,
            @RequestParam String descrizione,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // Recupero l'id del ticket
        Ticket ticket = ticketService.getTicketById(ticketId);

        // Recupero l'utente loggato
        User autore = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getName()));

        // Controllo se il ticket non è null
        if (ticket == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il ticket non esiste.");
            return "redirect:/admin/dashboard";
        }

        // Creo una nuova nota
        Note nota = new Note();
        nota.setDescrizione(descrizione);
        nota.setTicket(ticket);
        nota.setUser(autore);
        nota.setDataCreazione(LocalDateTime.now());

        // Salva la nota
        noteService.saveNote(nota);

        return "redirect:/ticket/" + ticketId;
    }
}