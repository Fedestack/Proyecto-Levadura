package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.ListaDePrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListaDePreciosRepository extends JpaRepository<ListaDePrecios, Long> {
    Optional<ListaDePrecios> findByNombre(String nombre);
}
