package br.com.confeitaria.sweet_manager.domain.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public abstract class ProdutoDecorator extends Produto {
    @Getter
    @JsonUnwrapped 
    protected Produto produtoEnfeitado;

    public ProdutoDecorator(Produto produto) {
        super(produto.getCategoriaUI());
        this.produtoEnfeitado = produto;
    }

    @Override
    public void aplicarCustomizacoes(Map<String, Object> dados) {
        produtoEnfeitado.aplicarCustomizacoes(dados);
    }

    @Override
    public String getBaseChave() {
        return produtoEnfeitado.getBaseChave();
    }

    @Override
    public List<String> getAdicionaisChaves() {
        return produtoEnfeitado.getAdicionaisChaves();
    }

    @Override
    public boolean isEmbalagemUnitaria() {
        return produtoEnfeitado.isEmbalagemUnitaria();
    }

    @Override
    public double getPrecoAdicionalTopo() {
        return produtoEnfeitado.getPrecoAdicionalTopo();
    }
}