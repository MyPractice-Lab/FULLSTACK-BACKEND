package com.thezone.tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrdenNuevaDTO(
        @NotBlank(message = "El estado de la orden es obligatorio.") String estado,
        @Valid @NotNull(message = "Los datos del cliente son obligatorios.") ClienteNuevoDTO cliente,
        @Valid @NotNull(message = "La dirección de entrega es obligatoria.") DireccionNuevaDTO direccion,
        @Valid @NotEmpty(message = "La orden debe tener al menos una línea.") List<LineaNuevaDTO> lineas) {

    public record ClienteNuevoDTO(
            @NotBlank(message = "El nombre del cliente es obligatorio.") String nombre,
            @NotBlank(message = "Los apellidos son obligatorios.") String apellidos,
            @NotBlank(message = "El correo es obligatorio.") @Email(message = "El correo no tiene un formato válido.") String correo) {
    }

    public record DireccionNuevaDTO(
            @NotBlank(message = "La calle es obligatoria.") String calle,
            String departamento,
            @NotBlank(message = "La región es obligatoria.") String region,
            @NotBlank(message = "La comuna es obligatoria.") String comuna,
            String indicaciones) {
    }

    public record LineaNuevaDTO(
            @NotNull(message = "Cada línea necesita el id del producto.") Long id,
            @NotNull(message = "Cada línea necesita una cantidad.") @Min(value = 1, message = "La cantidad mínima es 1.") Integer cantidad) {
    }
}
