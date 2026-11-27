package com.thezone.tienda.dto;

public record SesionDTO(String token, String tipo, long expiraEn, UsuarioDTO usuario) {

    public static SesionDTO de(String token, long expiraEn, UsuarioDTO usuario) {
        return new SesionDTO(token, "Bearer", expiraEn, usuario);
    }
}
