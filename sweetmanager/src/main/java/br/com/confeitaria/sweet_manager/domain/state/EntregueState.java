package br.com.confeitaria.sweet_manager.domain.state;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;

public class EntregueState implements StatusPedido {
    @Override
    public void avancar(Pedido p) {
        throw new IllegalStateException("Pedido já foi entregue.");
    }

    @Override
    public void cancelar(Pedido p) {
        throw new IllegalStateException("Não é possível cancelar um pedido entregue.");
    }

    @Override
    public String getDescricao() {
        return "Entregue";
    }
}