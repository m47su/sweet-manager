package br.com.confeitaria.sweet_manager.domain.state;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;

public class EmPreparacaoState implements StatusPedido {
    @Override
    public void avancar(Pedido p) {
        p.setStatusState(new EnviadoState());
    }

    @Override
    public void cancelar(Pedido p) {
        p.setStatusState(new CanceladoState());
    }

    @Override
    public String getDescricao() {
        return "Em Preparação";
    }
}