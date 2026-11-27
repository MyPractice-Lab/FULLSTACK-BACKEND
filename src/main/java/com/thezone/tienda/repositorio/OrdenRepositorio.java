package com.thezone.tienda.repositorio;

import com.thezone.tienda.modelo.Orden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepositorio extends JpaRepository<Orden, Long> {

    Optional<Orden> findByCodigo(String codigo);

    List<Orden> findByUsuarioId(Long usuarioId);

    List<Orden> findAllByOrderByNumeroDesc();
}
