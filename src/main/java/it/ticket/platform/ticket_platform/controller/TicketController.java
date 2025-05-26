package it.ticket.platform.ticket_platform.controller;

import java.security.Principal;
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
import it.ticket.platform.ticket_platform.model.Categoria;
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

    // METODI CRUD TICKET
    // Crea un ticket

    @GetMapping("/create")
    public String createTicket(Model model) {

        Ticket ticket = new Ticket();
        ticket.setStatus(Status.IN_LAVORAZIONE);
        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoryService.getAllCategories());
        model.addAttribute("operatori", userService.findUserDisponibile());
        return "ticket/create";
    }

    @PostMapping("/create")
    public String salvaTicket(@Valid @ModelAttribute("ticket") Ticket ticket,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Recupero l'operatore completo dal DB (l'id arriva dalla form: user.id)
        User operatore = userService.getUserById(ticket.getUser().getId());

        Categoria categoria = categoryService.getCategoriaById(ticket.getCategoria().getId());
        ticket.setCategoria(categoria);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            model.addAttribute("operatori", userService.findUserDisponibile());
            return "ticket/create";
        }

        if (operatore == null || !operatore.isDisponibile()) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            model.addAttribute("operatori", userService.findUserDisponibile());
            model.addAttribute("errorMessage", "Operatore non disponibile.");
            return "ticket/create";
        }

        // Assegno l’operatore
        ticket.setUser(operatore);

        // Salvo il ticket
        ticketService.saveTicket(ticket);

        // Rendo l’operatore non disponibile
        operatore.setDisponibile(false);
        userService.saveUser(operatore);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket creato con successo!");
        return "redirect:/admin/dashboard";
    }

    // Modifica del ticket
    @GetMapping("/edit/{id}")
    public String editTicket(@PathVariable("id") Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        List<User> operatoriDisponibili;
        if (ticket.getUser() != null) {
            operatoriDisponibili = userService.findUserDisponibile(ticket.getUser());
        } else {
            operatoriDisponibili = userService.findUserDisponibile();
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoryService.getAllCategories());
        model.addAttribute("operatori", operatoriDisponibili); // Recupera gli operatori disponibili
        return "ticket/edit";
    }

    // Post modifica ticket
    @PostMapping("/edit/{id}")
    public String updateTicket(@PathVariable Long id,
            @Valid @ModelAttribute("ticket") Ticket ticket,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Ticket ticketEsistente = ticketService.getTicketById(id);

        if (ticketEsistente == null) {
            return "redirect:/admin/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoryService.getAllCategories());
            model.addAttribute("operatori", userService.findUserDisponibile());
            return "ticket/edit";
        }

        User nuovoOperatore = userService.getUserById(ticket.getUser().getId());

        if (nuovoOperatore == null) {

            model.addAttribute("categorie", categoryService.getAllCategories());
            model.addAttribute("operatori", userService.findUserDisponibile());
            model.addAttribute("errorMessage", "Operatore non valido.");
            return "ticket/edit";
        }

        // Se è stato cambiato operatore: rendi disponibile il vecchio e occupato il
        // nuovo
        if (!ticketEsistente.getUser().getId().equals(nuovoOperatore.getId())) {
            // Rendi disponibile il vecchio
            User vecchioOperatore = ticketEsistente.getUser();
            vecchioOperatore.setDisponibile(true);
            userService.saveUser(vecchioOperatore);

            // Rendi non disponibile il nuovo
            nuovoOperatore.setDisponibile(false);
            userService.saveUser(nuovoOperatore);
        }

        // Aggiorna i campi modificabili
        ticketEsistente.setTitle(ticket.getTitle());
        ticketEsistente.setTesto(ticket.getTesto());
        ticketEsistente.setCategoria(categoryService.getCategoriaById(ticket.getCategoria().getId()));
        ticketEsistente.setUser(nuovoOperatore);

        ticketService.saveTicket(ticketEsistente);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket aggiornato con successo!");
        return "redirect:/admin/dashboard";
    }

    // Eliminazion del ticket
    // Cancellazione definitiva del ticket (solo admin)
    @PostMapping("/delete/{id}")
    public String deleteTicket(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,
            Principal principal) {
        Optional<User> optionalUser = userService.getUserByEmail(principal.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Controllo se l'utente è un admin
            if (user.getRole().getName().equals("ROLE_ADMIN")) {
                try {
                    Ticket ticket = ticketService.getTicketById(id);

                    // Cancella la nota associata (se esiste)
                    noteService.deleteByTicket(ticket);

                    // Rendo disponibile l'operatore assegnato al ticket
                    if (ticket.getUser() != null) {
                        User operatore = ticket.getUser();
                        operatore.setDisponibile(true);
                        userService.saveUser(operatore);
                    }

                    // Cancella definitivamente il ticket
                    ticketService.deleteById(id);

                    redirectAttributes.addFlashAttribute("successMessage", "Ticket eliminato con successo!");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione del ticket.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Non hai i permessi per eliminare questo ticket.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Utente non trovato.");
        }

        return "redirect:/admin/dashboard";
    }

    // Ricerca dei ticket per titolo
    @GetMapping("/search")
    public String search(Model model, @RequestParam(name = "keyword", required = false) String title) {
        List<Ticket> tickets = ticketService.findByTitleContainingIgnoreCase(title);
        model.addAttribute("list", tickets);
        model.addAttribute("keyword", title);
        return "admin/dashboard";
    }
}
