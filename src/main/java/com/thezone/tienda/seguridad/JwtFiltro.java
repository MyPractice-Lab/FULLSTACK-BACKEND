package com.thezone.tienda.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;
    private final DetalleUsuarioServicio detalleUsuarioServicio;

    public JwtFiltro(JwtServicio jwtServicio, DetalleUsuarioServicio detalleUsuarioServicio) {
        this.jwtServicio = jwtServicio;
        this.detalleUsuarioServicio = detalleUsuarioServicio;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest peticion,
            HttpServletResponse respuesta,
            FilterChain cadena) throws ServletException, IOException {

        String token = tokenDe(peticion);
        boolean sinAutenticar = SecurityContextHolder.getContext().getAuthentication() == null;

        if (token != null && sinAutenticar && jwtServicio.esValido(token)) {
            autenticar(peticion, token);
        }

        cadena.doFilter(peticion, respuesta);
    }

    private void autenticar(HttpServletRequest peticion, String token) {
        try {
            UserDetails detalle = detalleUsuarioServicio.loadUserByUsername(jwtServicio.correoDe(token));

            UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
                    detalle, null, detalle.getAuthorities());
            autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));

            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        } catch (UsernameNotFoundException error) {
            SecurityContextHolder.clearContext();
        }
    }

    private String tokenDe(HttpServletRequest peticion) {
        String cabecera = peticion.getHeader("Authorization");

        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            return null;
        }

        String token = cabecera.substring(7).trim();

        return token.isEmpty() ? null : token;
    }
}
