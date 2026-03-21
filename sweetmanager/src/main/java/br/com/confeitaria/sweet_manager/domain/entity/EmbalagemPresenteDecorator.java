package br.com.confeitaria.sweet_manager.domain.entity;


public class EmbalagemPresenteDecorator extends ProdutoDecorator {
    public EmbalagemPresenteDecorator(Produto produto) {
        super(produto);
    }

    @Override
    public String getNome() {
        return produtoEnfeitado.getNome() + " (Para Presente)";
    }

    @Override
    public Double getPreco() {
        if (isEmbalagemUnitaria())
            return produtoEnfeitado.getPreco() + 8.50;
        return produtoEnfeitado.getPreco();
    }

    @Override
    public double getPrecoTotal(int quantidade) {
        if (isEmbalagemUnitaria())
            return getPreco() * quantidade;
        return (produtoEnfeitado.getPreco() * quantidade) + 8.50;
    }

    @Override
    public void exibirDetalhes() {
        produtoEnfeitado.exibirDetalhes();
        System.out.println("  + Embalagem para Presente (R$ 8,50)");
    }

}
