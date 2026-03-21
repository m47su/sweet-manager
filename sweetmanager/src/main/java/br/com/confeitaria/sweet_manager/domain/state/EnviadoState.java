package br.com.confeitaria.sweet_manager.domain.state;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;

public class EnviadoState implements StatusPedido {
    @Override
    public void avancar(Pedido p) {
        p.setStatusState(new EntregueState());
    }

    @Override
    public void cancelar(Pedido p) {
        throw new IllegalStateException("Pedido já enviado. Não é possível cancelar.");
    }

    @Override
    public String getDescricao() {
        return "Enviado";
    }
}