package com.thezone.tienda.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Column(name = "cliente_nombre", length = 140)
    private String nombre;

    @Column(name = "cliente_apellidos", length = 140)
    private String apellidos;

    @Column(name = "cliente_correo", length = 160)
    private String correo;
}
