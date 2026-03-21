package br.com.confeitaria.sweet_manager.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rascunhos")
public class Rascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Lob
    @Column(name = "estado_produto")
    private byte[] estadoProduto;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Rascunho() {
    }

    public Rascunho(Usuario usuario, byte[] estadoProduto) {
        this.usuario = usuario;
        this.estadoProduto = estadoProduto;
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

   public void setUsuario(Usuario usuario) {
    this.usuario = usuario; 
}
    public byte[] getEstadoProduto() {
        return estadoProduto;
    }

    public void setEstadoProduto(byte[] estadoProduto) {
        this.estadoProduto = estadoProduto;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}