package com.thezone.tienda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoDTO(
        Long id,
        @NotBlank(message = "El nombre del producto es obligatorio.") String nombre,
        @NotBlank(message = "La categoría es obligatoria.") String categoria,
        @NotNull(message = "El precio es obligatorio.") @PositiveOrZero(message = "El precio no puede ser negativo.") Integer precio,
        @PositiveOrZero(message = "La oferta no puede ser negativa.") Integer oferta,
        @NotNull(message = "El stock es obligatorio.") @PositiveOrZero(message = "El stock no puede ser negativo.") Integer stock,
        String marca,
        String garantia,
        String imagen,
        String descripcion) {
}
