package com.fedestack.Levadura.controller;

import com.fedestack.Levadura.model.Producto;
import com.fedestack.Levadura.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController // Le dice a Spring que esta clase es un controlador REST
@RequestMapping("/api/v1/productos") // Todas las rutas de este controlador empezarán con /api/v1/productos

public class Productocontroller {

   @Autowired
   private ProductoService productoService;

   // MÉTODO MODIFICADO:
   // Ahora puede recibir un parámetro de consulta (query param) llamado "search".
   // Ejemplo: /api/v1/productos?search=medial
   @GetMapping
   public ResponseEntity<List<Producto>> listarProductos(
           @RequestParam(required = false) String search) {

      List<Producto> productos = productoService.searchProductos(Optional.ofNullable(search));
      return ResponseEntity.ok(productos);
   }
}
