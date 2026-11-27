package com.thezone.tienda.dto;

import jakarta.validation.constraints.NotBlank;

public record CredencialesDTO(
        @NotBlank(message = "El correo es obligatorio.") String correo,
        @NotBlank(message = "La clave es obligatoria.") String clave) {
}
