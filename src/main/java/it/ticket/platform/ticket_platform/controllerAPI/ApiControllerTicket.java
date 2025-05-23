package it.ticket.platform.ticket_platform.controllerAPI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.ticket.platform.ticket_platform.model.Categoria;
import it.ticket.platform.ticket_platform.model.Ticket;
import it.ticket.platform.ticket_platform.service.CategoryService;
import it.ticket.platform.ticket_platform.service.TicketService;
import it.ticket.platform.ticket_platform.enumeration.Status;

@RestController
@RequestMapping("/api/tickets")
public class ApiControllerTicket {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CategoryService categoryService;

    // Metodo per ottenere tutti i ticket da titolo
    @GetMapping
    public ResponseEntity<List<Ticket>> index(@RequestParam(name = "title", required = false) String title) {
        List<Ticket> ticket = ticketService.getTicket();
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    // Metodo per ottenere la categoria di un ticket
    // GET /api/tickets/categoria/3
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<Ticket>> getTicketsByCategoria(@PathVariable Long id) {
        Categoria categoria = categoryService.getCategoriaById(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build(); // Se la categoria non esiste, restituisce 404
        }

        List<Ticket> tickets = ticketService.getTicketsByCategoria(categoria);
        return ResponseEntity.ok(tickets);
    }

    // Metodo per ottenere lo status del ticket
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable String status) {
        try {
            Status ticketStatus = Status.valueOf(status.toUpperCase()); // converte stringa in enum
            List<Ticket> tickets = ticketService.getTicketsByStatus(ticketStatus);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // status non valido
        }
    }
}
