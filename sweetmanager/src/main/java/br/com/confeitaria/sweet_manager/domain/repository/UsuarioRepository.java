package br.com.confeitaria.sweet_manager.domain.repository;

import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}