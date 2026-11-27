package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.ProductoDTO;
import com.thezone.tienda.modelo.Producto;
import com.thezone.tienda.repositorio.ProductoRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServicio {

    private static final int STOCK_CRITICO = 5;
    private static final String IMAGEN_POR_DEFECTO = "/assets/img/portada.jpg";

    private final ProductoRepositorio productos;
    private final CategoriaServicio categoriaServicio;

    public ProductoServicio(ProductoRepositorio productos, CategoriaServicio categoriaServicio) {
        this.productos = productos;
        this.categoriaServicio = categoriaServicio;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listar() {
        return productos.findAll().stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(String slug) {
        categoriaServicio.buscarPorSlug(slug);
        return productos.findByCategoriaSlug(slug).stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarCriticos() {
        return productos.findByStockLessThanEqual(STOCK_CRITICO).stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtener(Long id) {
        return Mapeador.aDTO(buscar(id));
    }

    public ProductoDTO crear(ProductoDTO datos) {
        Producto producto = new Producto();
        aplicar(producto, datos);
        return Mapeador.aDTO(productos.save(producto));
    }

    public ProductoDTO actualizar(Long id, ProductoDTO datos) {
        Producto producto = buscar(id);
        aplicar(producto, datos);
        return Mapeador.aDTO(productos.save(producto));
    }

    public void eliminar(Long id) {
        productos.delete(buscar(id));
    }

    Producto buscar(Long id) {
        return productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el producto con id " + id + "."));
    }

    private void aplicar(Producto producto, ProductoDTO datos) {
        if (datos.oferta() != null && datos.oferta() > 0 && datos.oferta() >= datos.precio()) {
            throw new ReglaNegocioExcepcion("La oferta debe ser menor que el precio normal.");
        }

        producto.setNombre(datos.nombre());
        producto.setCategoria(categoriaServicio.buscarPorSlug(datos.categoria()));
        producto.setPrecio(datos.precio());
        producto.setOferta(datos.oferta() != null && datos.oferta() > 0 ? datos.oferta() : null);
        producto.setStock(datos.stock());
        producto.setMarca(datos.marca());
        producto.setGarantia(datos.garantia());
        producto.setImagen(datos.imagen() == null || datos.imagen().isBlank() ? IMAGEN_POR_DEFECTO : datos.imagen());
        producto.setDescripcion(datos.descripcion());
    }
}
