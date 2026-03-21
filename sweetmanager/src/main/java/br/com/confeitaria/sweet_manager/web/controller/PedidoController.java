package br.com.confeitaria.sweet_manager.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.confeitaria.sweet_manager.domain.entity.EmbalagemPresenteDecorator;
import br.com.confeitaria.sweet_manager.domain.entity.ItemPedido;
import br.com.confeitaria.sweet_manager.domain.entity.Pedido;
import br.com.confeitaria.sweet_manager.domain.entity.Produto;
import br.com.confeitaria.sweet_manager.domain.entity.TopoDeBoloDecorator;
import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import br.com.confeitaria.sweet_manager.domain.repository.PedidoRepository;
import br.com.confeitaria.sweet_manager.domain.repository.UsuarioRepository;
import br.com.confeitaria.sweet_manager.domain.service.ProdutoRegistry;
import br.com.confeitaria.sweet_manager.domain.service.RascunhoService;
import br.com.confeitaria.sweet_manager.domain.state.RecebidoState;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private ProdutoRegistry registry;
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RascunhoService rascunhoService;

@GetMapping("/todos")
public List<Pedido> listarTodosPedidos() {
    return pedidoRepository.findAll(); 
}

@PostMapping("/{id}/avancar")
public void avancarPedido(@PathVariable Long id) {
    Pedido pedido = pedidoRepository.findById(id).orElseThrow();
    pedido.avancarEstado(); 
    pedidoRepository.save(pedido);
}

@PostMapping("/{id}/cancelar")
public void cancelarPedido(@PathVariable Long id) {
    Pedido pedido = pedidoRepository.findById(id).orElseThrow();
    pedido.cancelarPedido(); 
    pedidoRepository.save(pedido);
}

    @GetMapping("/meus-pedidos")
    public List<Pedido> listarMeusPedidos(Authentication auth) {
        return pedidoRepository.findByUsuarioEmail(auth.getName());
    }

    @GetMapping("/catalogo")
    public Map<String, Map<String, String>> obterCatalogo() {
        return registry.getCatalogo();
    }

    @PostMapping("/rascunho/salvar")
    public void salvarRascunho(@RequestBody Map<String, Object> dados, Authentication auth) {
        String baseChave = (String) dados.get("base");

        Produto produto = registry.buscarPrototipo(baseChave).clone();
        produto.setBaseChave(baseChave);

        produto.setQuantidade((Integer) dados.get("quantidade"));

        produto.aplicarCustomizacoes(dados);

        Produto produtoFinal = produto;

        List<String> adicionais = (List<String>) dados.get("adicionais");
        if (adicionais != null) {
            for (String topo : adicionais) {
                produtoFinal.registrarAdicional(topo);
                produtoFinal = new TopoDeBoloDecorator(produtoFinal, topo);
            }
        }

        if (Boolean.TRUE.equals(dados.get("comEmbalagem"))) {
            produtoFinal.registrarAdicional("EMBALAGEM"); 
            produtoFinal = new EmbalagemPresenteDecorator(produtoFinal);
        }

        rascunhoService.salvarEstado(auth.getName(), produtoFinal);
    }

    @GetMapping("/rascunho/restaurar")
    public Produto restaurar(Authentication auth) {
       
        return rascunhoService.restaurarUltimo(auth.getName());
    }

    @PostMapping("/montar")
    public Map<String, Object> montarPedido(@RequestBody Map<String, Object> dados, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        List<Map<String, Object>> itensEntrada = (List<Map<String, Object>>) dados.get("itens");

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatusState(new RecebidoState());
        pedido.setTipoEntrega((String) dados.get("tipoEntrega"));

        double precoTotalGeral = 0;
        List<ItemPedido> listaItens = new ArrayList<>();

        for (Map<String, Object> itemDados : itensEntrada) {
            int qtd = (int) itemDados.getOrDefault("quantidade", 1);
            Produto pFinal = registry.buscarPrototipo((String) itemDados.get("base")).clone();

            // 1. Aplica customizações (recheios, etc)
            pFinal.aplicarCustomizacoes(itemDados);

            // 2. Aplica Decorators (Topos e Embalagem)
            List<Map<String, String>> extras = (List<Map<String, String>>) itemDados.get("adicionais");
            if (extras != null) {
                for (Map<String, String> extra : extras) {
                    if ("TOPO".equals(extra.get("tipo"))) {
                        pFinal = new TopoDeBoloDecorator(pFinal, extra.get("escolha"));
                    } else if ("EMBALAGEM".equals(extra.get("tipo"))) {
                        pFinal = new EmbalagemPresenteDecorator(pFinal);
                    }
                }
            }
            double totalDaLinha = pFinal.getPrecoTotal(qtd);
            precoTotalGeral += totalDaLinha;

            ItemPedido item = new ItemPedido();
            item.setDescricao(pFinal.getNome());
            item.setQuantidade(qtd);
            item.setPrecoUnitario(totalDaLinha / qtd);
            item.setPedido(pedido);
            listaItens.add(item);
        }

        pedido.setItens(listaItens);
        double taxaEntrega = "DELIVERY".equals(pedido.getTipoEntrega()) ? 15.0 : 0.0;
        pedido.setPrecoTotal(precoTotalGeral + taxaEntrega);
        pedidoRepository.save(pedido);

        return Map.of("id", pedido.getId(), "total", pedido.getPrecoTotal());
    }

}