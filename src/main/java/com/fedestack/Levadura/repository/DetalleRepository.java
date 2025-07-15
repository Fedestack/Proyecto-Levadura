package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DetalleRepository extends JpaRepository<DetallePedido, Long> {
}
