package com.thezone.tienda.web;

import com.thezone.tienda.dto.OrdenDTO;
import com.thezone.tienda.dto.OrdenNuevaDTO;
import com.thezone.tienda.servicio.OrdenServicio;
import com.thezone.tienda.servicio.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Creación de órdenes de compra y consulta del historial")
public class OrdenControlador {

    private final OrdenServicio servicio;
    private final UsuarioServicio usuarioServicio;

    public OrdenControlador(OrdenServicio servicio, UsuarioServicio usuarioServicio) {
        this.servicio = servicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    @Operation(summary = "Lista todas las órdenes", description = "Requiere token de administrador.")
    public List<OrdenDTO> listar() {
        return servicio.listar();
    }

    @GetMapping("/mias")
    @Operation(summary = "Lista las órdenes de la cuenta autenticada")
    public List<OrdenDTO> listarMias(Authentication autenticacion) {
        Long usuarioId = usuarioServicio.obtenerPorCorreo(autenticacion.getName()).id();
        return servicio.listarDeUsuario(usuarioId);
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtiene una orden por su código", description = "Se usa en la boleta tras el pago.")
    public OrdenDTO obtener(@PathVariable String codigo) {
        return servicio.obtenerPorCodigo(codigo);
    }

    @PostMapping
    @Operation(
            summary = "Crea una orden de compra",
            description = "El servidor calcula precios, subtotales y totales, y descuenta stock si el pago fue aprobado. "
                    + "Si viene un token, la orden queda asociada a esa cuenta.")
    public ResponseEntity<OrdenDTO> crear(
            @Valid @RequestBody OrdenNuevaDTO datos,
            Authentication autenticacion) {

        String correo = autenticacion == null ? null : autenticacion.getName();

        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos, correo));
    }

    @PatchMapping("/{codigo}/estado")
    @Operation(summary = "Cambia el estado de una orden", description = "Requiere token de administrador.")
    public OrdenDTO actualizarEstado(
            @PathVariable String codigo,
            @RequestBody Map<String, String> cuerpo) {

        return servicio.actualizarEstado(codigo, cuerpo.get("estado"));
    }
}
