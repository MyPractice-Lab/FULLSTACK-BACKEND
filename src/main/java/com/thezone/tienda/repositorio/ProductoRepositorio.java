package com.thezone.tienda.repositorio;

import com.thezone.tienda.modelo.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaSlug(String slug);

    List<Producto> findByStockLessThanEqual(Integer stock);

    boolean existsByCategoriaId(Long categoriaId);
}
