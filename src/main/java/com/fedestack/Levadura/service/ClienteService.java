package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.Cliente;
import com.fedestack.Levadura.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

   @Autowired
   private ClienteRepository clienteRepository;

   /**
    * Obtiene todos los clientes de la base de datos.
    *
    * @return una lista de todos los clientes.
    */
   public List<Cliente> getAllClientes() {
      return clienteRepository.findAll();
   }

   /**
    * Busca un cliente por su ID.
    *
    * @param id El ID del cliente a buscar.
    * @return un Optional que contiene al cliente si se encuentra, o vacío si no.
    */
   public Optional<Cliente> getClienteById(Long id) {
      return clienteRepository.findById(id);
   }

   /**
    * Guarda un nuevo cliente o actualiza uno existente.
    *
    * @param cliente El cliente a guardar o actualizar.
    * @return el cliente guardado.
    */
   public Cliente saveCliente(Cliente cliente) {
      return clienteRepository.save(cliente);
   }

   /**
    * Elimina un cliente por su ID.
    *
    * @param id El ID del cliente a eliminar.
    */
   public void deleteCliente(Long id) {
      clienteRepository.deleteById(id);
   }
}
