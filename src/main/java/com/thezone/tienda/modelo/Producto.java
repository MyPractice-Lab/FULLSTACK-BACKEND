package com.thezone.tienda.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer precio;

    @PositiveOrZero
    private Integer oferta;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock;

    @Column(length = 80)
    private String marca;

    @Column(length = 80)
    private String garantia;

    @Column(length = 250)
    private String imagen;

    @Column(length = 1000)
    private String descripcion;

    public Integer precioFinal() {
        boolean enOferta = oferta != null && oferta > 0 && oferta < precio;
        return enOferta ? oferta : precio;
    }
}
