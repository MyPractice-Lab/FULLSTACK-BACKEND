package com.thezone.tienda.dto;

import java.util.List;

public record OrdenDTO(
        String codigo,
        Integer numero,
        String fecha,
        String estado,
        Long usuarioId,
        ClienteDTO cliente,
        DireccionDTO direccion,
        List<LineaDTO> lineas,
        TotalesDTO totales) {

    public record ClienteDTO(String nombre, String apellidos, String correo) {
    }

    public record DireccionDTO(
            String calle,
            String departamento,
            String region,
            String comuna,
            String indicaciones) {
    }

    public record LineaDTO(
            Long id,
            String nombre,
            String imagen,
            Integer precio,
            Integer cantidad,
            Integer subtotal) {
    }

    public record TotalesDTO(
            Integer neto,
            Integer iva,
            Integer bruto,
            Integer despacho,
            Integer total,
            Integer unidades) {
    }
}
