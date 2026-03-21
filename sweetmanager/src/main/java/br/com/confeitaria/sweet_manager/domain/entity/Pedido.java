package br.com.confeitaria.sweet_manager.domain.entity;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.com.confeitaria.sweet_manager.domain.bridge.DeliveryProprio;
import br.com.confeitaria.sweet_manager.domain.bridge.EntregaImplementacao;
import br.com.confeitaria.sweet_manager.domain.bridge.RetiradaNoLocal;
import br.com.confeitaria.sweet_manager.domain.state.CanceladoState;
import br.com.confeitaria.sweet_manager.domain.state.EmPreparacaoState;
import br.com.confeitaria.sweet_manager.domain.state.EntregueState;
import br.com.confeitaria.sweet_manager.domain.state.EnviadoState;
import br.com.confeitaria.sweet_manager.domain.state.RecebidoState;
import br.com.confeitaria.sweet_manager.domain.state.StatusPedido;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preco_total", nullable = false)
    private Double precoTotal;

    @Transient
    private StatusPedido statusAtual;

    @Column(name = "status_string")
    private String status;

    @Column(name = "tipo_entrega")
    private String tipoEntrega;

    @Column(name = "data_pedido")
    private LocalDateTime dataPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Transient
    private EntregaImplementacao entrega;

    @PrePersist
    protected void onCreate() {
        this.dataPedido = LocalDateTime.now();
    }

    @PostLoad
    private void reconstruirPadroes() {
        if (this.status != null) {
            this.statusAtual = switch (this.status) {
                case "Em Preparação" -> new EmPreparacaoState();
                case "Enviado" -> new EnviadoState();
                case "Entregue" -> new EntregueState();
                case "Cancelado" -> new CanceladoState();
                default -> new RecebidoState();
            };
        }

        if (this.tipoEntrega != null) {
            this.entrega = "DELIVERY".equals(this.tipoEntrega)
                    ? new DeliveryProprio()
                    : new RetiradaNoLocal();
        }
    }

    public void setStatusState(StatusPedido novoEstado) {
        this.statusAtual = novoEstado;
        this.status = novoEstado.getDescricao();
    }

    public String obterLogistica() {
        return (entrega != null) ? entrega.processarEntrega() : "Pendente";
    }

    public void avancarEstado() {
        statusAtual.avancar(this);
    }


    public void cancelarPedido() {
        if ("Enviado".equals(this.status)) {
            throw new IllegalStateException("Pedidos enviados não podem ser cancelados!");
        }
        statusAtual.cancelar(this);
    }

    public void proximoPasso() {
        this.statusAtual.avancar(this);
    }

    public void abortarPedido() {
        try {
            this.statusAtual.cancelar(this);
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

}
