package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.PedidoAutomatico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoAutomaticoRepository extends JpaRepository<PedidoAutomatico, Long> {
    List<PedidoAutomatico> findByClienteId(Long clienteId);
}
