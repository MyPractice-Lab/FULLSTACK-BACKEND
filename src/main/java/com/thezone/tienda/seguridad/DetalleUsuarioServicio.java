package com.thezone.tienda.seguridad;

import com.thezone.tienda.modelo.Usuario;
import com.thezone.tienda.repositorio.UsuarioRepositorio;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetalleUsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarios;

    public DetalleUsuarioServicio(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(correo.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("No existe una cuenta con ese correo."));

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getClave())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getRol().autoridad())))
                .disabled(Boolean.FALSE.equals(usuario.getActivo()))
                .build();
    }
}
