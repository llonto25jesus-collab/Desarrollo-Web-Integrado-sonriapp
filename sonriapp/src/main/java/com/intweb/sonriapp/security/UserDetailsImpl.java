package com.intweb.sonriapp.security;

import com.intweb.sonriapp.model.Usuario;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserDetailsImpl {

    public static User build(Usuario usuario) {
        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre()))
        );
    }
}