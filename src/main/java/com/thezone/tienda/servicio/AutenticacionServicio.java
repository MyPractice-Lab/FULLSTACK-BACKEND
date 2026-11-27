package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.CredencialesDTO;
import com.thezone.tienda.dto.SesionDTO;
import com.thezone.tienda.dto.UsuarioAltaDTO;
import com.thezone.tienda.dto.UsuarioDTO;
import com.thezone.tienda.modelo.Rol;
import com.thezone.tienda.modelo.Usuario;
import com.thezone.tienda.repositorio.UsuarioRepositorio;
import com.thezone.tienda.seguridad.JwtServicio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AutenticacionServicio {

    private final UsuarioRepositorio usuarios;
    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder codificador;
    private final JwtServicio jwtServicio;

    public AutenticacionServicio(
            UsuarioRepositorio usuarios,
            UsuarioServicio usuarioServicio,
            PasswordEncoder codificador,
            JwtServicio jwtServicio) {
        this.usuarios = usuarios;
        this.usuarioServicio = usuarioServicio;
        this.codificador = codificador;
        this.jwtServicio = jwtServicio;
    }

    public SesionDTO iniciarSesion(CredencialesDTO credenciales) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(credenciales.correo().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasExcepcion("No existe una cuenta con ese correo."));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasExcepcion("La cuenta está desactivada. Escríbenos para reactivarla.");
        }

        if (!codificador.matches(credenciales.clave(), usuario.getClave())) {
            throw new CredencialesInvalidasExcepcion("La contraseña no coincide con la cuenta.");
        }

        return sesionDe(usuario);
    }

    @Transactional
    public SesionDTO registrar(UsuarioAltaDTO datos) {
        UsuarioAltaDTO comoCliente = new UsuarioAltaDTO(
                datos.nombre(),
                datos.correo(),
                datos.clave(),
                Rol.CLIENTE,
                datos.region(),
                datos.comuna(),
                datos.calle(),
                Boolean.TRUE);

        UsuarioDTO creado = usuarioServicio.crear(comoCliente);

        return SesionDTO.de(
                jwtServicio.generar(creado.correo(), creado.rol().name()),
                jwtServicio.duracionSegundos(),
                creado);
    }

    public UsuarioDTO usuarioAutenticado(String correo) {
        return usuarioServicio.obtenerPorCorreo(correo);
    }

    private SesionDTO sesionDe(Usuario usuario) {
        return SesionDTO.de(
                jwtServicio.generar(usuario.getCorreo(), usuario.getRol().name()),
                jwtServicio.duracionSegundos(),
                Mapeador.aDTO(usuario));
    }
}
