package it.ticket.platform.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.ticket.platform.ticket_platform.enumeration.Status;
import it.ticket.platform.ticket_platform.model.Categoria;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Trova i ticket per titolo
    List<Ticket> findByTitleContainingIgnoreCase(String title);
        
    //Trova tutti i ticket assegnati agli operatori
    List<Ticket> findByUser(User user);

    //Filtra i ticket per status
    List<Ticket> findByStatus(Status status);

    //Filtra i ticket per categoria
    List<Ticket> findByCategoria(Categoria categoria);

    //Filtra per titolo
    List<Ticket> findByTitleContains(String title);

}
