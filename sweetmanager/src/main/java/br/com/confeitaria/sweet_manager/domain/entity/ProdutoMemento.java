package br.com.confeitaria.sweet_manager.domain.entity;

public class ProdutoMemento {
    private final Produto estado; 

    public ProdutoMemento(Produto produto) {
        this.estado = produto; 
    }

    public Produto getEstadoRestaurado() {
        return estado;
    }
}