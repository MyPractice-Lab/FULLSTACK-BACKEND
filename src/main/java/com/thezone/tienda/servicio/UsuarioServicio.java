package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.UsuarioAltaDTO;
import com.thezone.tienda.dto.UsuarioDTO;
import com.thezone.tienda.dto.UsuarioEdicionDTO;
import com.thezone.tienda.modelo.Rol;
import com.thezone.tienda.modelo.Usuario;
import com.thezone.tienda.repositorio.UsuarioRepositorio;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServicio {

    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder codificador;

    public UsuarioServicio(UsuarioRepositorio usuarios, PasswordEncoder codificador) {
        this.usuarios = usuarios;
        this.codificador = codificador;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return usuarios.findAll().stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtener(Long id) {
        return Mapeador.aDTO(buscar(id));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorCorreo(String correo) {
        return Mapeador.aDTO(buscarPorCorreo(correo));
    }

    public UsuarioDTO crear(UsuarioAltaDTO datos) {
        String correo = normalizarCorreo(datos.correo());

        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioExcepcion("Ya existe una cuenta con el correo " + correo + ".");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre());
        usuario.setCorreo(correo);
        usuario.setClave(codificador.encode(datos.clave()));
        usuario.setRol(datos.rol() == null ? Rol.CLIENTE : datos.rol());
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setCalle(datos.calle());
        usuario.setActivo(datos.activo() == null ? Boolean.TRUE : datos.activo());

        return Mapeador.aDTO(usuarios.save(usuario));
    }

    public UsuarioDTO actualizar(Long id, UsuarioEdicionDTO datos) {
        Usuario usuario = buscar(id);
        String correo = normalizarCorreo(datos.correo());

        boolean correoTomado = !correo.equalsIgnoreCase(usuario.getCorreo())
                && usuarios.existsByCorreoIgnoreCase(correo);

        if (correoTomado) {
            throw new ReglaNegocioExcepcion("El correo " + correo + " ya está usado por otra cuenta.");
        }

        usuario.setNombre(datos.nombre());
        usuario.setCorreo(correo);
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setCalle(datos.calle());

        if (datos.rol() != null) {
            usuario.setRol(datos.rol());
        }

        if (datos.activo() != null) {
            usuario.setActivo(datos.activo());
        }

        if (datos.clave() != null && !datos.clave().isBlank()) {
            usuario.setClave(codificador.encode(datos.clave()));
        }

        return Mapeador.aDTO(usuarios.save(usuario));
    }

    public void eliminar(Long id) {
        Usuario usuario = buscar(id);

        boolean ultimoAdministrador = usuario.getRol() == Rol.ADMINISTRADOR
                && usuarios.findAll().stream().filter(otro -> otro.getRol() == Rol.ADMINISTRADOR).count() == 1;

        if (ultimoAdministrador) {
            throw new ReglaNegocioExcepcion("No se puede eliminar al único administrador de la tienda.");
        }

        usuarios.delete(usuario);
    }

    Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el usuario con id " + id + "."));
    }

    Usuario buscarPorCorreo(String correo) {
        return usuarios.findByCorreoIgnoreCase(normalizarCorreo(correo))
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe una cuenta con ese correo."));
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}
