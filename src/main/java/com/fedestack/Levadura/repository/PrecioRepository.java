package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.ListaDePrecios;
import com.fedestack.Levadura.model.Precio;
import com.fedestack.Levadura.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {
    Optional<Precio> findByProductoAndListaDePrecios(Producto producto, ListaDePrecios listaDePrecios);
}
