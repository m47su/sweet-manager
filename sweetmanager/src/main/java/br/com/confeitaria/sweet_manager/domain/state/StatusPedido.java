package br.com.confeitaria.sweet_manager.domain.state;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;

public interface StatusPedido {
    void avancar(Pedido pedido);
    void cancelar(Pedido pedido);
    String getDescricao();
}

