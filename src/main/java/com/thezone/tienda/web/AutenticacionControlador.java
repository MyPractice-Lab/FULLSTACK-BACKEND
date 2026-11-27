package com.thezone.tienda.web;

import com.thezone.tienda.dto.CredencialesDTO;
import com.thezone.tienda.dto.SesionDTO;
import com.thezone.tienda.dto.UsuarioAltaDTO;
import com.thezone.tienda.dto.UsuarioDTO;
import com.thezone.tienda.servicio.AutenticacionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Emisión de tokens JWT, registro de clientes y validación de la sesión")
public class AutenticacionControlador {

    private final AutenticacionServicio servicio;

    public AutenticacionControlador(AutenticacionServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Inicia sesión y devuelve un token JWT",
            description = "El token se envía en la cabecera Authorization como 'Bearer <token>'.")
    public SesionDTO login(@Valid @RequestBody CredencialesDTO credenciales) {
        return servicio.iniciarSesion(credenciales);
    }

    @PostMapping("/registro")
    @Operation(
            summary = "Registra un cliente nuevo y lo deja con sesión iniciada",
            description = "El rol siempre queda como cliente: no se puede crear un administrador desde aquí.")
    public ResponseEntity<SesionDTO> registro(@Valid @RequestBody UsuarioAltaDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(datos));
    }

    @GetMapping("/yo")
    @Operation(
            summary = "Devuelve la cuenta dueña del token",
            description = "El frontend lo llama al recargar la página para revalidar la sesión guardada.")
    public UsuarioDTO yo(Authentication autenticacion) {
        return servicio.usuarioAutenticado(autenticacion.getName());
    }
}
