package com.thezone.tienda.web;

import com.thezone.tienda.dto.UsuarioAltaDTO;
import com.thezone.tienda.dto.UsuarioDTO;
import com.thezone.tienda.dto.UsuarioEdicionDTO;
import com.thezone.tienda.modelo.Rol;
import com.thezone.tienda.servicio.ReglaNegocioExcepcion;
import com.thezone.tienda.servicio.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de cuentas; el listado y la eliminación son solo de administrador")
public class UsuarioControlador {

    private final UsuarioServicio servicio;

    public UsuarioControlador(UsuarioServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista las cuentas", description = "Requiere token de administrador.")
    public List<UsuarioDTO> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una cuenta", description = "Un cliente solo puede ver la propia.")
    public UsuarioDTO obtener(@PathVariable Long id, Authentication autenticacion) {
        exigirPropiaOAdministrador(id, autenticacion);
        return servicio.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea una cuenta desde el panel", description = "Requiere token de administrador.")
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioAltaDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una cuenta", description = "Un cliente solo puede editar la propia y no puede cambiarse el rol.")
    public UsuarioDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioEdicionDTO datos,
            Authentication autenticacion) {

        exigirPropiaOAdministrador(id, autenticacion);

        boolean administrador = esAdministrador(autenticacion);

        if (!administrador && datos.rol() != null && datos.rol() != Rol.CLIENTE) {
            throw new ReglaNegocioExcepcion("No puedes cambiar tu propio rol.");
        }

        UsuarioEdicionDTO datosPermitidos = administrador
                ? datos
                : new UsuarioEdicionDTO(
                        datos.nombre(),
                        datos.correo(),
                        datos.clave(),
                        null,
                        datos.region(),
                        datos.comuna(),
                        datos.calle(),
                        null);

        return servicio.actualizar(id, datosPermitidos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una cuenta", description = "Requiere token de administrador.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void exigirPropiaOAdministrador(Long id, Authentication autenticacion) {
        if (esAdministrador(autenticacion)) {
            return;
        }

        UsuarioDTO propia = servicio.obtenerPorCorreo(autenticacion.getName());

        if (!propia.id().equals(id)) {
            throw new AccesoDenegadoExcepcion("Solo puedes ver o editar tu propia cuenta.");
        }
    }

    private boolean esAdministrador(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> "ROLE_ADMINISTRADOR".equals(autoridad.getAuthority()));
    }
}
