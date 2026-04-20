package com.intweb.sonriapp.service;

import com.intweb.sonriapp.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Integer id;
    private final String correo;
    private final String password;
    private final boolean activo;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(Usuario usuario) {
        this.id = usuario.getId();
        this.correo = usuario.getCorreo();
        this.password = usuario.getContrasena();
        this.activo = usuario.getActivo();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())
        );
    }

    public static UserDetailsImpl build(Usuario usuario) {
        return new UserDetailsImpl(usuario);
    }

    @Override public String getUsername()               { return correo; }
    @Override public String getPassword()               { return password; }
    @Override public boolean isEnabled()                { return activo; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}