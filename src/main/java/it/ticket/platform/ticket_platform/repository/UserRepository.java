package it.ticket.platform.ticket_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.ticket.platform.ticket_platform.model.User;

public interface UserRepository extends JpaRepository<User, Long> {



    // Filtra l'utente per email
    Optional<User> findByEmail(String email);

    //Trova gli operatori disponibili
    List<User> findByDisponibileTrue();

    //Trova gli operatori non disponilibi
    List<User> findByDisponibileFalse();

}
