package com.fedestack.Levadura.repository;

import java.util.List;

import com.fedestack.Levadura.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Le indica a Spring que esta es una interfaz de repositorio

public interface ProductoRepository extends JpaRepository<Producto, Long> {
   // NUEVO MÉTODO:
   // Spring Data JPA creará automáticamente una consulta que busca productos
   // cuyo nombre contenga la cadena de texto proporcionada, ignorando mayúsculas/minúsculas.
   // Ejemplo: si searchTerm es "medial", encontrará "Medialuna de Manteca".
   List<Producto> findByNombreContainingIgnoreCase(String searchTerm);
}
