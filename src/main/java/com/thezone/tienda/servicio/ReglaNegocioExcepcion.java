package com.thezone.tienda.servicio;

public class ReglaNegocioExcepcion extends RuntimeException {

    public ReglaNegocioExcepcion(String mensaje) {
        super(mensaje);
    }
}
