package br.com.confeitaria.sweet_manager.domain.bridge;

public class DeliveryProprio implements EntregaImplementacao {
    @Override
    public String processarEntrega() {
        return "Enviado via motoboy da confeitaria.";
    }
}
