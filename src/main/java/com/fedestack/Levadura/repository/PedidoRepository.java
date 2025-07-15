package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
   // Método personalizado para buscar todos los pedidos de un cliente específico
   List<Pedido> findByClienteId(Long clienteId);
}
