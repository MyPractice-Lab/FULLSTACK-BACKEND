package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.OrdenDTO;
import com.thezone.tienda.dto.OrdenNuevaDTO;
import com.thezone.tienda.modelo.Cliente;
import com.thezone.tienda.modelo.Direccion;
import com.thezone.tienda.modelo.Orden;
import com.thezone.tienda.modelo.OrdenLinea;
import com.thezone.tienda.modelo.Producto;
import com.thezone.tienda.modelo.Totales;
import com.thezone.tienda.modelo.Usuario;
import com.thezone.tienda.repositorio.OrdenRepositorio;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdenServicio {

    private static final int COSTO_DESPACHO = 4990;
    private static final int MINIMO_DESPACHO_GRATIS = 100000;
    private static final Set<String> ESTADOS = Set.of("pagada", "rechazada");

    private final OrdenRepositorio ordenes;
    private final ProductoServicio productoServicio;
    private final UsuarioServicio usuarioServicio;

    public OrdenServicio(
            OrdenRepositorio ordenes,
            ProductoServicio productoServicio,
            UsuarioServicio usuarioServicio) {
        this.ordenes = ordenes;
        this.productoServicio = productoServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @Transactional(readOnly = true)
    public List<OrdenDTO> listar() {
        return ordenes.findAllByOrderByNumeroDesc().stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public OrdenDTO obtenerPorCodigo(String codigo) {
        return Mapeador.aDTO(buscarPorCodigo(codigo));
    }

    @Transactional(readOnly = true)
    public List<OrdenDTO> listarDeUsuario(Long usuarioId) {
        return ordenes.findByUsuarioId(usuarioId).stream().map(Mapeador::aDTO).toList();
    }

    public OrdenDTO crear(OrdenNuevaDTO datos, String correoAutenticado) {
        if (!ESTADOS.contains(datos.estado())) {
            throw new ReglaNegocioExcepcion("El estado de la orden debe ser 'pagada' o 'rechazada'.");
        }

        boolean aprobada = "pagada".equals(datos.estado());

        Orden orden = new Orden();
        orden.setEstado(datos.estado());
        orden.setNumero(siguienteNumero());
        orden.setCodigo(generarCodigo(orden.getNumero()));
        orden.setUsuario(usuarioDe(correoAutenticado));
        orden.setCliente(clienteDe(datos));
        orden.setDireccion(direccionDe(datos));

        int bruto = 0;
        int unidades = 0;

        for (OrdenNuevaDTO.LineaNuevaDTO entrada : datos.lineas()) {
            Producto producto = productoServicio.buscar(entrada.id());

            if (aprobada && producto.getStock() < entrada.cantidad()) {
                throw new ReglaNegocioExcepcion(
                        "No hay stock suficiente de " + producto.getNombre()
                                + ". Quedan " + producto.getStock() + " unidades.");
            }

            int precio = producto.precioFinal();
            int subtotal = precio * entrada.cantidad();

            OrdenLinea linea = new OrdenLinea();
            linea.setProductoId(producto.getId());
            linea.setNombre(producto.getNombre());
            linea.setImagen(producto.getImagen());
            linea.setPrecio(precio);
            linea.setCantidad(entrada.cantidad());
            linea.setSubtotal(subtotal);
            orden.agregarLinea(linea);

            bruto += subtotal;
            unidades += entrada.cantidad();

            if (aprobada) {
                producto.setStock(Math.max(0, producto.getStock() - entrada.cantidad()));
            }
        }

        orden.setTotales(calcularTotales(bruto, unidades));

        return Mapeador.aDTO(ordenes.save(orden));
    }

    public OrdenDTO actualizarEstado(String codigo, String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new ReglaNegocioExcepcion("El estado de la orden debe ser 'pagada' o 'rechazada'.");
        }

        Orden orden = buscarPorCodigo(codigo);
        orden.setEstado(estado);

        return Mapeador.aDTO(ordenes.save(orden));
    }

    Totales calcularTotales(int bruto, int unidades) {
        int iva = Math.round(bruto - (bruto / 1.19f));
        int despacho = bruto == 0 || bruto > MINIMO_DESPACHO_GRATIS ? 0 : COSTO_DESPACHO;

        Totales totales = new Totales();
        totales.setBruto(bruto);
        totales.setIva(iva);
        totales.setNeto(bruto - iva);
        totales.setDespacho(despacho);
        totales.setTotal(bruto + despacho);
        totales.setUnidades(unidades);

        return totales;
    }

    private Orden buscarPorCodigo(String codigo) {
        return ordenes.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la orden " + codigo + "."));
    }

    private Usuario usuarioDe(String correoAutenticado) {
        if (correoAutenticado == null) {
            return null;
        }

        return usuarioServicio.buscarPorCorreo(correoAutenticado);
    }

    private int siguienteNumero() {
        return ordenes.findAllByOrderByNumeroDesc().stream()
                .mapToInt(Orden::getNumero)
                .max()
                .orElse(0) + 1;
    }

    private String generarCodigo(int numero) {
        return "ORDER" + String.format("%05d", numero);
    }

    private Cliente clienteDe(OrdenNuevaDTO datos) {
        Cliente cliente = new Cliente();
        cliente.setNombre(datos.cliente().nombre().trim());
        cliente.setApellidos(datos.cliente().apellidos().trim());
        cliente.setCorreo(datos.cliente().correo().trim().toLowerCase());
        return cliente;
    }

    private Direccion direccionDe(OrdenNuevaDTO datos) {
        Direccion direccion = new Direccion();
        direccion.setCalle(datos.direccion().calle().trim());
        direccion.setDepartamento(texto(datos.direccion().departamento()));
        direccion.setRegion(datos.direccion().region());
        direccion.setComuna(datos.direccion().comuna());
        direccion.setIndicaciones(texto(datos.direccion().indicaciones()));
        return direccion;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
