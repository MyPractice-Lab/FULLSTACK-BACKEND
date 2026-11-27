package com.thezone.tienda.servicio;

public class CredencialesInvalidasExcepcion extends RuntimeException {

    public CredencialesInvalidasExcepcion(String mensaje) {
        super(mensaje);
    }
}
