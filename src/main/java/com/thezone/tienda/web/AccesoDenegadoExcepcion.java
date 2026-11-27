package com.thezone.tienda.web;

public class AccesoDenegadoExcepcion extends RuntimeException {

    public AccesoDenegadoExcepcion(String mensaje) {
        super(mensaje);
    }
}
