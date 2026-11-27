package com.thezone.tienda.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServicio {

    private final SecretKey llave;
    private final long duracionMs;

    public JwtServicio(
            @Value("${tienda.jwt.secreto}") String secreto,
            @Value("${tienda.jwt.duracion-minutos}") long duracionMinutos) {
        this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionMs = duracionMinutos * 60 * 1000;
    }

    public String generar(String correo, String rol) {
        Date ahora = new Date();

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(llave)
                .compact();
    }

    public String correoDe(String token) {
        return claims(token).getSubject();
    }

    public String rolDe(String token) {
        return claims(token).get("rol", String.class);
    }

    public boolean esValido(String token) {
        try {
            claims(token);
            return true;
        } catch (JwtException | IllegalArgumentException error) {
            return false;
        }
    }

    public long duracionSegundos() {
        return duracionMs / 1000;
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
