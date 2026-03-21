package br.com.confeitaria.sweet_manager.domain.entity;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bombom extends Produto {
    private String tipoChocolate;
    private String recheio;

    @Override
    public Double getPreco() {
        return getPrecoBase(); 
    }

    @Override
    public void exibirDetalhes() {
        System.out
                .println("Bombom: " + getNome() + " | Tipo de Chocolate: " + tipoChocolate + " | Recheio: " + recheio);
    }

    public Bombom() {
        super("BOMBOM");
    }

    @Override
    public void aplicarCustomizacoes(Map<String, Object> dados) {
        String chocolate = (String) dados.get("chocolate");
        if (chocolate != null && !"Padrão".equalsIgnoreCase(chocolate)) {
            this.tipoChocolate = chocolate;
        }
    }

    @Override
    public boolean isEmbalagemUnitaria() {
        return false;
    }

    @Override
    public double getPrecoAdicionalTopo() {
        return 1.0;
    }
}