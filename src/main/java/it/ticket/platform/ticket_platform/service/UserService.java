package it.ticket.platform.ticket_platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ticket.platform.ticket_platform.model.User;
import it.ticket.platform.ticket_platform.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    //Ottengo tutti gli utenti
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    //Trovo l'user per mail
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    //Trova l'user per disponibilità ed ID
    public boolean isUserDisponibile(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get().isDisponibile();
        } else{
            return false; //Se l'utente non esiste non è disponibile
        }
    }
}
