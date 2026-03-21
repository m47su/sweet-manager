package br.com.confeitaria.sweet_manager.domain.entity;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bolo extends Produto {
    private String massa;
    private String recheio;
    private String cobertura;

    @Override
    public void exibirDetalhes() {
        System.out.println("Bolo: " + getNome() + " | Massa: " + massa + " | Recheio: " + recheio);
    }

    @Override
    public Double getPreco() {
        return getPrecoBase(); // preço base para bolos, pode ser ajustado com adicionais
    }

    @Override
    public void aplicarCustomizacoes(Map<String, Object> dados) {
        this.recheio = (String) dados.get("recheio");
    }

    public Bolo() {
        super("BOLO");
    }

    @Override
    public boolean isEmbalagemUnitaria() {
        return true;
    }

    @Override
    public double getPrecoAdicionalTopo() { return 5.0; }
}                         