package com.fedestack.Levadura.repository;

import com.fedestack.Levadura.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   // Spring Data JPA es tan inteligente que si defines un método con un nombre específico,
   // él solo crea la consulta. Este método buscará un usuario por su email.
   Optional<Usuario> findByEmail(String email);
}
