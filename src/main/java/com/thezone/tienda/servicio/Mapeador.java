package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.CategoriaDTO;
import com.thezone.tienda.dto.OrdenDTO;
import com.thezone.tienda.dto.ProductoDTO;
import com.thezone.tienda.dto.UsuarioDTO;
import com.thezone.tienda.modelo.Categoria;
import com.thezone.tienda.modelo.Orden;
import com.thezone.tienda.modelo.OrdenLinea;
import com.thezone.tienda.modelo.Producto;
import com.thezone.tienda.modelo.Usuario;
import java.util.List;

public final class Mapeador {

    private Mapeador() {
    }

    public static CategoriaDTO aDTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getSlug(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getImagen());
    }

    public static ProductoDTO aDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria().getSlug(),
                producto.getPrecio(),
                producto.getOferta(),
                producto.getStock(),
                producto.getMarca(),
                producto.getGarantia(),
                producto.getImagen(),
                producto.getDescripcion());
    }

    public static UsuarioDTO aDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getRegion(),
                usuario.getComuna(),
                usuario.getCalle(),
                usuario.getActivo());
    }

    public static OrdenDTO aDTO(Orden orden) {
        List<OrdenDTO.LineaDTO> lineas = orden.getLineas().stream()
                .map(Mapeador::aDTO)
                .toList();

        return new OrdenDTO(
                orden.getCodigo(),
                orden.getNumero(),
                orden.getFecha().toString(),
                orden.getEstado(),
                orden.getUsuario() == null ? null : orden.getUsuario().getId(),
                new OrdenDTO.ClienteDTO(
                        orden.getCliente().getNombre(),
                        orden.getCliente().getApellidos(),
                        orden.getCliente().getCorreo()),
                new OrdenDTO.DireccionDTO(
                        orden.getDireccion().getCalle(),
                        orden.getDireccion().getDepartamento(),
                        orden.getDireccion().getRegion(),
                        orden.getDireccion().getComuna(),
                        orden.getDireccion().getIndicaciones()),
                lineas,
                new OrdenDTO.TotalesDTO(
                        orden.getTotales().getNeto(),
                        orden.getTotales().getIva(),
                        orden.getTotales().getBruto(),
                        orden.getTotales().getDespacho(),
                        orden.getTotales().getTotal(),
                        orden.getTotales().getUnidades()));
    }

    private static OrdenDTO.LineaDTO aDTO(OrdenLinea linea) {
        return new OrdenDTO.LineaDTO(
                linea.getProductoId(),
                linea.getNombre(),
                linea.getImagen(),
                linea.getPrecio(),
                linea.getCantidad(),
                linea.getSubtotal());
    }
}
