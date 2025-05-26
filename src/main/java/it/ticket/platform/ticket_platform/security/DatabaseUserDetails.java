package it.ticket.platform.ticket_platform.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.ticket.platform.ticket_platform.model.Role;
import it.ticket.platform.ticket_platform.model.User;

public class DatabaseUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public DatabaseUserDetails(User user) {
        this.username = user.getEmail(); 
        this.password = user.getPassword();
        this.authorities = new ArrayList<>();

        Role role = user.getRole(); // L'utente ha solo un ruolo
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
            //log
            System.out.println("AUTORITÀ: " + role.getName().toUpperCase());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    // Questi metodi servono per abilitare l'account (li lasciamo sempre "true")
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
