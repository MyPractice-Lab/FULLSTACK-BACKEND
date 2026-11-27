package com.thezone.tienda.dto;

import com.thezone.tienda.modelo.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioAltaDTO(
        @NotBlank(message = "El nombre es obligatorio.") String nombre,
        @NotBlank(message = "El correo es obligatorio.") @Email(message = "El correo no tiene un formato válido.") String correo,
        @NotBlank(message = "La clave es obligatoria.") @Size(min = 6, message = "La clave debe tener al menos 6 caracteres.") String clave,
        Rol rol,
        String region,
        String comuna,
        String calle,
        Boolean activo) {
}
