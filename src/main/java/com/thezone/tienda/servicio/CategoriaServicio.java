package com.thezone.tienda.servicio;

import com.thezone.tienda.dto.CategoriaDTO;
import com.thezone.tienda.modelo.Categoria;
import com.thezone.tienda.repositorio.CategoriaRepositorio;
import com.thezone.tienda.repositorio.ProductoRepositorio;
import java.text.Normalizer;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaServicio {

    private final CategoriaRepositorio categorias;
    private final ProductoRepositorio productos;

    public CategoriaServicio(CategoriaRepositorio categorias, ProductoRepositorio productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        return categorias.findAll().stream().map(Mapeador::aDTO).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorSlug(String slug) {
        return Mapeador.aDTO(buscarPorSlug(slug));
    }

    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Long id) {
        return Mapeador.aDTO(buscarPorId(id));
    }

    public CategoriaDTO crear(CategoriaDTO datos) {
        String slug = slugDe(datos);

        if (categorias.existsBySlug(slug)) {
            throw new ReglaNegocioExcepcion("Ya existe una categoría con el identificador " + slug + ".");
        }

        Categoria categoria = new Categoria();
        categoria.setSlug(slug);
        categoria.setNombre(datos.nombre());
        categoria.setDescripcion(datos.descripcion());
        categoria.setImagen(imagenODefecto(datos.imagen()));

        return Mapeador.aDTO(categorias.save(categoria));
    }

    public CategoriaDTO actualizar(Long id, CategoriaDTO datos) {
        Categoria categoria = buscarPorId(id);
        String slug = slugDe(datos);

        boolean slugTomado = !slug.equals(categoria.getSlug()) && categorias.existsBySlug(slug);

        if (slugTomado) {
            throw new ReglaNegocioExcepcion("Ya existe otra categoría con el identificador " + slug + ".");
        }

        categoria.setSlug(slug);
        categoria.setNombre(datos.nombre());
        categoria.setDescripcion(datos.descripcion());
        categoria.setImagen(imagenODefecto(datos.imagen()));

        return Mapeador.aDTO(categorias.save(categoria));
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);

        if (productos.existsByCategoriaId(id)) {
            throw new ReglaNegocioExcepcion(
                    "La categoría " + categoria.getNombre() + " tiene productos asociados y no se puede eliminar.");
        }

        categorias.delete(categoria);
    }

    Categoria buscarPorSlug(String slug) {
        return categorias.findBySlug(slug)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la categoría " + slug + "."));
    }

    Categoria buscarPorId(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la categoría con id " + id + "."));
    }

    private String slugDe(CategoriaDTO datos) {
        boolean traeSlug = datos.slug() != null && !datos.slug().isBlank();
        return crearSlug(traeSlug ? datos.slug() : datos.nombre());
    }

    private String imagenODefecto(String imagen) {
        boolean vacia = imagen == null || imagen.isBlank();
        return vacia ? "/assets/img/portada.jpg" : imagen;
    }

    static String crearSlug(String texto) {
        String sinTildes = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return sinTildes.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
