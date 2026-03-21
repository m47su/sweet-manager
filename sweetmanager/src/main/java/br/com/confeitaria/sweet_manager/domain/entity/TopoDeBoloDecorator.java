package br.com.confeitaria.sweet_manager.domain.entity;


public class TopoDeBoloDecorator extends ProdutoDecorator{
    private String tipoTopo;

    public TopoDeBoloDecorator(Produto produto, String tipoTopo) {
        super(produto);
        this.tipoTopo = tipoTopo;
    }

    @Override
    public String getNome() {
        return produtoEnfeitado.getNome() + " + Topo de " + tipoTopo;
    }

    @Override
    public Double getPreco() {
        return produtoEnfeitado.getPreco() + getPrecoAdicionalTopo();
    }

    @Override
    public void exibirDetalhes() {
        produtoEnfeitado.exibirDetalhes();
        double valorTopo = produtoEnfeitado.getNome().toLowerCase().contains("bombom") ? 1.0 : 5.0;
        System.out.println(" + Topo Personalizado: R$ " + String.format("%.2f", valorTopo));
    }
}
