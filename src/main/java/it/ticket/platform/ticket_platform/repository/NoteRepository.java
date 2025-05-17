package it.ticket.platform.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.ticket.platform.ticket_platform.model.Note;
import it.ticket.platform.ticket_platform.model.Ticket;
//import it.ticket.platform.ticket_platform.model.User;

public interface NoteRepository extends JpaRepository<Note, Long> {
    
        //Filtra tutte le note di associate al ticket in ordine di data di arrivo
        List<Ticket> findByTicketOrderCreationDesc(Ticket ticket);

        //Filtra tutte le note scritte da un operatore
        //List<Note> findByUser (User user);
}   
