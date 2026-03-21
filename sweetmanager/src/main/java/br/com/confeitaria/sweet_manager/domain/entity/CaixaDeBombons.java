package br.com.confeitaria.sweet_manager.domain.entity;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class CaixaDeBombons extends Produto {
    private List<Produto> itens = new ArrayList<>();
    private int quantidadeBombons;

    public void adicionar(Produto p) {
        itens.add(p);
    }

    @Override
    public Double getPreco() {
        return getPrecoBase(); 
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("Caixa de Bombons: " + getNome() + " | Descrição: " + getDescricao());
        System.out.println("Itens na caixa:");
        for (Produto p : itens) {
            p.exibirDetalhes();
        }
    }
    
    public CaixaDeBombons() {
        super("CAIXA");
    }

    @Override
    public void aplicarCustomizacoes(Map<String, Object> dados) {
        if (dados.containsKey("quantidadeBombons")) {
            this.quantidadeBombons = (int) dados.get("quantidadeBombons");
        }
    }

    @Override
    public boolean isEmbalagemUnitaria() {
        return false; 
    }

    @Override
    public double getPrecoAdicionalTopo() {
        return 0.0; 
    }
}