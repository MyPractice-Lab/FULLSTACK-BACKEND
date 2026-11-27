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
public class Direccion {

    @Column(name = "direccion_calle", length = 200)
    private String calle;

    @Column(name = "direccion_departamento", length = 80)
    private String departamento;

    @Column(name = "direccion_region", length = 120)
    private String region;

    @Column(name = "direccion_comuna", length = 120)
    private String comuna;

    @Column(name = "direccion_indicaciones", length = 400)
    private String indicaciones;
}
