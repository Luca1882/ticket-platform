package it.ticket.platform.ticket_platform.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ticket.platform.ticket_platform.model.Note;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.repository.NoteRepository;
import it.ticket.platform.ticket_platform.repository.TicketRepository;

@Service
public class NoteService {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    // Trovo tutte le note
    public List<Note> getAllNote() {
        return noteRepository.findAll();
    }

    // Trovo le note per ID
    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    // Aggiungere una nota al ticket
    public Note addNote(Note note, Long ticketId, String userEmail) {
        // Recupero il ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Il ticket con ID " + ticketId + " non esiste"));

        // Recupero l'operatore da email
        User user = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("L'utente con email " + userEmail + " non esiste"));

        // Creazione nota con i dettagli
        note.setTicket(ticket);
        note.setUser(user);
        note.setDataCreazione(LocalDateTime.now());
        return noteRepository.save(note);
    }

    // Modificare la nota
    public Note updateNote(Long id, Note note) {
        // Controllo se la nota esiste
        Note noteToUpdate = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La nota con ID " + id + " non esiste"));

        // Aggiorna i campi desiderati
        noteToUpdate.setDescrizione(note.getDescrizione());
        noteToUpdate.setDataCreazione(note.getDataCreazione());
        // aggiungi altri campi da aggiornare se necessario

        return noteRepository.save(noteToUpdate);
    }

    // Elimina la nota
    public void deleteNote(Long id){
        // COntrollo se la nota esiste
        Note noteToDelete = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La nota con ID " + id + " non esiste"));
                // Eliminazione
                noteRepository.delete(noteToDelete);
    }
}