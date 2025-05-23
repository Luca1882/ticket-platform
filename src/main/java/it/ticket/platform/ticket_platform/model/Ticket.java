package it.ticket.platform.ticket_platform.model;

import java.time.LocalDateTime;
import java.util.List;

import it.ticket.platform.ticket_platform.enumeration.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;




@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // ENUM per lo stato del ticket (APERTO, IN_CORSO, CHIUSO)
    @NotNull(message = "Status è obbligatorio")
    private Status status;

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione;

    @Column(name = "data_chiusura")
    private LocalDateTime dataChiusura;

    @Size(min= 5, max= 20)
    @NotBlank(message = "Il titolo è obbligatorio")
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Chi gestisce il ticket (può essere assegnato più tardi)
    private User user;

    
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)// Categoria del Ticket
    private Categoria categoria;
    

    @Size(max = 500)
    @NotBlank(message= "Il testo è obbligatoria")
    private String testo;

    @OneToMany(mappedBy = "ticket")
    private List<Note> notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataChiusura() {
        return dataChiusura;
    }

    public void setDataChiusura(LocalDateTime dataChiusura) {
        this.dataChiusura = dataChiusura;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescrizione() {
        return testo;
    }

    public void setDescrizione(String descrizione) {
        this.testo = descrizione;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
    
    
    
}
