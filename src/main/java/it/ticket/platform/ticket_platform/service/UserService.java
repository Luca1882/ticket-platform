package it.ticket.platform.ticket_platform.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    // Ottengo tutti gli utenti
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    // Trovo l'user per mail
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Trova l'user per ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("L'utente con ID " + id + " non esiste"));
    }

    // Trova l'user per disponibilità ed ID
    public boolean isUserDisponibile(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get().isDisponibile();
        } else {
            return false; // Se l'utente non esiste non è disponibile
        }
    }

    // Trova l'user disponibile
    public List<User> findUserDisponibile() {
        List<User> utenti = userRepository.findAll();
        return utenti.stream()
                .filter(u -> u.getRole().getName().equalsIgnoreCase("ROLE_USER") && u.isDisponibile())
                .toList();
    }

    // Ritrova l'user disponibile più quello inserito
    public List<User> findUserDisponibile(User user) {
        List<User> utenti = userRepository.findAll();
        return utenti.stream()
                .filter(u -> (u.getRole().getName().equalsIgnoreCase("ROLE_USER") && u.isDisponibile()) 
                || (Objects.equals(u.getId(), user.getId())))
                .toList();
    }

    // Salva l'utente
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
