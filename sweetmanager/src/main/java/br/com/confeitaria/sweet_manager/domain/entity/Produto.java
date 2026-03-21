package br.com.confeitaria.sweet_manager.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Produto implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private Double precoBase;
    private String descricao;
    private Integer quantidade; // para o memento
    private String categoriaUI;
    private String baseChave;  
    private List<String> adicionaisChaves = new ArrayList<>();

    public abstract void aplicarCustomizacoes(Map<String, Object> dados);
    @Override
    public Produto clone () {
        try {
            return (Produto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar o produto", e);
        }
    }

    public void registrarAdicional(String chave) {
        this.adicionaisChaves.add(chave);
    }

    public ProdutoMemento criarRascunho() {
        return new ProdutoMemento(this.clone());
    }

    public Produto(String categoriaUI) {
        this.categoriaUI = categoriaUI;
    }
    public abstract void exibirDetalhes();

    public abstract Double getPreco();

    public abstract boolean isEmbalagemUnitaria();

    public abstract double getPrecoAdicionalTopo();

    public double getPrecoTotal(int quantidade) {
        return getPreco() * quantidade;
    }
   
}
