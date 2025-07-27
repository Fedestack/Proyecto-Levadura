package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.Direccion;
import com.fedestack.Levadura.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    public List<Direccion> getDireccionesByClienteId(Long clienteId) {
        return direccionRepository.findByClienteId(clienteId);
    }

    public Optional<Direccion> getDireccionById(Long id) {
        return direccionRepository.findById(id);
    }

    public Direccion saveDireccion(Direccion direccion) {
        return direccionRepository.save(direccion);
    }

    public void deleteDireccion(Long id) {
        direccionRepository.deleteById(id);
    }
}
