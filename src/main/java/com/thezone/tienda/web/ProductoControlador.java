package com.thezone.tienda.web;

import com.thezone.tienda.dto.ProductoDTO;
import com.thezone.tienda.servicio.ProductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD del catálogo de productos")
public class ProductoControlador {

    private final ProductoServicio servicio;

    public ProductoControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista productos", description = "Con el parámetro categoria filtra por el slug de una categoría.")
    public List<ProductoDTO> listar(@RequestParam(required = false) String categoria) {
        boolean sinFiltro = categoria == null || categoria.isBlank() || "todas".equals(categoria);
        return sinFiltro ? servicio.listar() : servicio.listarPorCategoria(categoria);
    }

    @GetMapping("/criticos")
    @Operation(summary = "Lista productos con stock crítico", description = "Requiere token de administrador.")
    public List<ProductoDTO> listarCriticos() {
        return servicio.listarCriticos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto por su id")
    public ProductoDTO obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un producto", description = "Requiere token de administrador.")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un producto", description = "Requiere token de administrador.")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto", description = "Requiere token de administrador.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
