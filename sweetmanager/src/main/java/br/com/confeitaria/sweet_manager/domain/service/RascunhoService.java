package br.com.confeitaria.sweet_manager.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import br.com.confeitaria.sweet_manager.domain.entity.Produto;
import br.com.confeitaria.sweet_manager.domain.entity.ProdutoMemento;
import br.com.confeitaria.sweet_manager.domain.entity.Rascunho;
import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import br.com.confeitaria.sweet_manager.domain.repository.RascunhoRepository;
import br.com.confeitaria.sweet_manager.domain.repository.UsuarioRepository;
import java.io.Serializable;


@Service
public class RascunhoService {
    @Autowired private RascunhoRepository repository;
    @Autowired private UsuarioRepository usuarioRepository;

    public void salvarRascunho(String email, Produto produtoAtual) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        
        ProdutoMemento memento = produtoAtual.criarRascunho();

        byte[] dados = SerializationUtils.serialize((Serializable) memento.getEstadoRestaurado());
        
        Rascunho entidade = new Rascunho(usuario, dados);
        repository.save(entidade);
    }

    public void salvarEstado(String email, Produto produto) {
        // 1. Busca o objeto Usuario completo usando o e-mail
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto rascunho = produto.clone();
        byte[] dados = SerializationUtils.serialize(rascunho);
        
        Rascunho entidade = new Rascunho();
        entidade.setUsuario(usuario); 
        entidade.setEstadoProduto(dados);
        
        repository.save(entidade);
    }

    public Produto restaurarUltimo(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Rascunho ultimo = repository.findFirstByUsuarioOrderByDataCriacaoDesc(usuario);
        
        if (ultimo != null) {
            Produto p = (Produto) SerializationUtils.deserialize(ultimo.getEstadoProduto());
            repository.delete(ultimo);
            return p;
        }
        return null;
    }
}
