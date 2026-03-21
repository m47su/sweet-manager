package br.com.confeitaria.sweet_manager.domain.state;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;

public class CanceladoState implements StatusPedido {
    @Override
    public void avancar(Pedido p) {
        throw new IllegalStateException("Pedido cancelado não pode avançar.");
    }

    @Override
    public void cancelar(Pedido p) {
        throw new IllegalStateException("Pedido já está cancelado.");
    }

    @Override
    public String getDescricao() {
        return "Cancelado";
    }
}