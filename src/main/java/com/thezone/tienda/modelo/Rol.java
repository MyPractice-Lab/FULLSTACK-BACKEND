package com.thezone.tienda.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Rol {
    ADMINISTRADOR,
    CLIENTE;

    @JsonValue
    public String valorJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Rol desdeJson(String texto) {
        if (texto == null || texto.isBlank()) {
            return CLIENTE;
        }

        return Rol.valueOf(texto.trim().toUpperCase());
    }

    public String autoridad() {
        return "ROLE_" + name();
    }
}
