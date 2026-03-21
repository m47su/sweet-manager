package br.com.confeitaria.sweet_manager.domain.repository;

import br.com.confeitaria.sweet_manager.domain.entity.Rascunho;
import br.com.confeitaria.sweet_manager.domain.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RascunhoRepository extends JpaRepository<Rascunho, Long> {
    Rascunho findFirstByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}