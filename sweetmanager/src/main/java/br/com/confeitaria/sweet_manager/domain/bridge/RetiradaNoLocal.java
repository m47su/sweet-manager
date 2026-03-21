package br.com.confeitaria.sweet_manager.domain.bridge;

public class RetiradaNoLocal implements EntregaImplementacao {
    @Override
    public String processarEntrega() {
        return "Disponível para retirada no balcão.";
    }
}