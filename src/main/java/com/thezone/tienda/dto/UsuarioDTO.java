package com.thezone.tienda.dto;

import com.thezone.tienda.modelo.Rol;

public record UsuarioDTO(
        Long id,
        String nombre,
        String correo,
        Rol rol,
        String region,
        String comuna,
        String calle,
        Boolean activo) {
}
