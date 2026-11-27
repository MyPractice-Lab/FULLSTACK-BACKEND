package com.thezone.tienda.web;

import com.thezone.tienda.dto.CategoriaDTO;
import com.thezone.tienda.servicio.CategoriaServicio;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "CRUD del catálogo de categorías de la tienda")
public class CategoriaControlador {

    private final CategoriaServicio servicio;

    public CategoriaControlador(CategoriaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista todas las categorías", description = "Endpoint público usado por el catálogo.")
    public List<CategoriaDTO> listar() {
        return servicio.listar();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obtiene una categoría por su slug")
    public CategoriaDTO obtener(@PathVariable String slug) {
        return servicio.obtenerPorSlug(slug);
    }

    @PostMapping
    @Operation(summary = "Crea una categoría", description = "Requiere token de administrador.")
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una categoría", description = "Requiere token de administrador.")
    public CategoriaDTO actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una categoría sin productos asociados")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
