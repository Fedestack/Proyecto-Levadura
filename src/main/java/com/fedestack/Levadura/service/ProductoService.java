package com.fedestack.Levadura.service;

import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos de la base de datos.
     * @return una lista de todos los productos.
     */
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su ID.
     * @param id El ID del producto a buscar.
     * @return un Optional que contiene el producto si se encuentra, o vacío si no.
     */
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Guarda un nuevo producto o actualiza uno existente.
     * @param producto El producto a guardar o actualizar.
     * @return el producto guardado.
     */
    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     */
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Busca productos por un término de búsqueda o devuelve todos si no hay término.
     * @param searchTerm El término de búsqueda (puede ser Optional).
     * @return una lista de productos que coinciden con la búsqueda.
     */
    public List<Producto> searchProductos(Optional<String> searchTerm) {
        return searchTerm.map(term -> productoRepository.findByNombreContainingIgnoreCase(term))
                .orElse(productoRepository.findAll());
    }
}