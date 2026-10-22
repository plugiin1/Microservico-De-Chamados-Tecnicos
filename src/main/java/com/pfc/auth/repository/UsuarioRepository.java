package com.pfc.auth.repository;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    long countByRoleAndAtivoTrue(Role role);
}
