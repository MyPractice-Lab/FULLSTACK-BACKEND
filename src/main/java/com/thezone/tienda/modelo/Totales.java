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
public class Totales {

    @Column(name = "total_neto")
    private Integer neto;

    @Column(name = "total_iva")
    private Integer iva;

    @Column(name = "total_bruto")
    private Integer bruto;

    @Column(name = "total_despacho")
    private Integer despacho;

    @Column(name = "total_total")
    private Integer total;

    @Column(name = "total_unidades")
    private Integer unidades;
}
