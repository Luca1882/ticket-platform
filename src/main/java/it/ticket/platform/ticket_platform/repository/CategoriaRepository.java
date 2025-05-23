package it.ticket.platform.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.ticket.platform.ticket_platform.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
        
    //Creata per la lettura di Categoria
    //Ricerca la categoria per Id
    
}
