package it.ticket.platform.ticket_platform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ticket.platform.ticket_platform.model.Categoria;
import it.ticket.platform.ticket_platform.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    @Autowired
    CategoriaRepository categoriaRepository;

    //Ricerca tutte le categorie
    public List<Categoria> getAllCategories(){
        return categoriaRepository.findAll();
    }

    //Ricerca la categoria per ID
    public Categoria getCategoriaById(Long id){
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria con ID " + id + " non trovata."));
    }

    //Ricerca la categoria 
}
