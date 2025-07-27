package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.MetodoPago;
import com.fedestack.Levadura.repository.MetodoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> getMetodosPagoByClienteId(Long clienteId) {
        return metodoPagoRepository.findByClienteId(clienteId);
    }

    public Optional<MetodoPago> getMetodoPagoById(Long id) {
        return metodoPagoRepository.findById(id);
    }

    public MetodoPago saveMetodoPago(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    public void deleteMetodoPago(Long id) {
        metodoPagoRepository.deleteById(id);
    }
}
