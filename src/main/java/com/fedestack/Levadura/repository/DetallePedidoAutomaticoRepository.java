package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.DetallePedidoAutomatico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoAutomaticoRepository extends JpaRepository<DetallePedidoAutomatico, Long> {
}
