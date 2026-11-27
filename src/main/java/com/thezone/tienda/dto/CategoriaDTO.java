package com.thezone.tienda.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaDTO(
        Long id,
        String slug,
        @NotBlank(message = "El nombre de la categoría es obligatorio.") String nombre,
        String descripcion,
        String imagen) {
}
