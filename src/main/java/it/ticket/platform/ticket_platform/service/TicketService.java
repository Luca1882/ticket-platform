package it.ticket.platform.ticket_platform.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.repository.TicketRepository;
import it.ticket.platform.ticket_platform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    // Trova ticket
    public List<Ticket> getTicket() {
        return ticketRepository.findAll();
    }

    // Recupera un singolo ticket per ID (gestione errore se non trovato)
    public Ticket getTicketById(Long id) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            return ticketOptional.get();
        } else {
            throw new EntityNotFoundException("Ticket con ID " + id + " non trovato.");
        }
    }

    // Ricerca dele ticket per titolo
    public List<Ticket> findTicketByTitle(String title) {
        return ticketRepository.findByTitleContains(title);
    }

    // Creo e salvo ticket se ci sono operatori disponibili, altrimenti non faccio
    // nulla
    public Ticket creaTicket(Ticket addTicket) {
        if (addTicket.getUser() == null) {
            // Se non è disponibile, ne cerco uno a cui assegnarlo
            List<User> disponibile = userRepository.findByDisponibileTrue();

            if (disponibile.isEmpty()) {
                throw new RuntimeException("Non ci sono operatori disponibili");
            }

            // Altrimenti se ci sono operatori prendo il primo disponibile
            User user = disponibile.get(0);
            user.setDisponibile(false);
            userRepository.save(user); // Salvo la modifica
            addTicket.setUser(user); // Assegno l'operatore al ticket
        }
        addTicket.setDataCreazione(LocalDateTime.now()); // Imposto la data di creazione
        return ticketRepository.save(addTicket);
    }
    // Modifica del ticket
    public Ticket aggiornaTicket(Long id, Ticket showTicket){
        Ticket ticket = ticketRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Il ticket con ID " + id + " non è stato trovato."));

        ticket.setTitle(showTicket.getTitle());
        ticket.setTesto(showTicket.getTesto());
        ticket.setCategoria(showTicket.getCategoria());
        ticket.setStatus(showTicket.getStatus());

        return ticketRepository.save(ticket);
    }
    //Elimino il ticket
    public void eliminaTicket(Long id){
        Ticket ticket = ticketRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Il ticket con ID " + id + " non esiste."));
        ticketRepository.delete(ticket);
    }
}
