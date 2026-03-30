package br.com.confeitaria.sweet_manager.domain.service;

import br.com.confeitaria.sweet_manager.domain.entity.Bolo;
import br.com.confeitaria.sweet_manager.domain.entity.Bombom;
import br.com.confeitaria.sweet_manager.domain.entity.Produto;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProdutoRegistry {
    private Map<String, Produto> prototipos = new HashMap<>();

    public ProdutoRegistry() {
        // BOLOS
        adicionarBolo("BOLO_CHOCOLATE", "Bolo de Chocolate", "Cobertura de chocolate",
                "Bolo artesanal com massa de chocolate, com recheio de brigadeiro e cobertura de chocolate, por apenas R$ 50,00",
                "Chocolate", "Brigadeiro", 50.0);
        adicionarBolo("BOLO_MORANGO", "Bolo de Morango", "Cobertura de morango",
                "Bolo artesanal com massa de pão de ló, com recheio de morango e cobertura de morango, por apenas R$ 55,00",
                "Pão de ló", "Morango", 55.0);
        adicionarBolo("BOLO_LIMAO", "Bolo de Limão", "Cobertura de limão",
                "Bolo artesanal com massa de Limão, com recheio de mousse de limão e cobertura de limão, por apenas R$ 48,00",
                "Limão", "Mousse de Limão", 48.0);
        adicionarBolo("BOLO_LEITE_NINHO", "Bolo de Leite Ninho", "Cobertura de leite ninho",
                "Bolo artesanal com massa de pão de ló, com recheio de creme de leite ninho e cobertura de leite ninho, por apenas R$ 60,00",
                "Pão de ló", "Creme de Ninho", 60.0);
        adicionarBolo("BOLO_FLORESTA_NEGRA", "Bolo Floresta Negra", "Cobertura de chocolate",
                "Bolo artesanal com massa de chocolate, com recheio de chantilly e cerejas e cobertura de chocolate, por apenas R$ 65,00",
                "Chocolate", "Chantilly e Cereja", 65.0);

        // BOMBONS
        adicionarBombom("BOMBOM_CHOCOLATE", "Bombom de Chocolate",
                "Bombom artesanal de chocolate ao leite e recheio de ganache, por apenas R$ 3,00", "Ao Leite",
                "Ganache", 3.0);
        adicionarBombom("BOMBOM_MORANGO", "Bombom de Morango",
                "Bombom artesanal de chocolate ao leite e recheiro de morango fresco, por apenas R$ 4,00", "Ao Leite",
                "Morango", 4.0);
        adicionarBombom("BOMBOM_LIMAO", "Bombom de Limão",
                "Bombom artesanal de chocolate branco e recheio cremoso de mousse de limão, por apenas R$ 3,50",
                "Branco", "Mousse de Limão", 3.5);
        adicionarBombom("BOMBOM_MARACUJA", "Bombom de Maracujá",
                "Bombom artesanal de chocolate ao leite e recheio cremoso de mousse de maracujá, por apenas R$ 3,50",
                "Ao Leite", "Mousse de Maracujá", 3.5);
        adicionarBombom("BOMBOM_LEITE_NINHO", "Bombom de Ninho",
                "Bombom artesanal de chocolate branco e recheio leite ninho, por apenas R$ 4,50", "Branco",
                "Leite de Ninho", 4.5);
        adicionarBombom("BOMBOM_CEREJA", "Bombom de Cereja",
                "Bombom artesanal de chocolate meio amargo e recheio de cereja, por apenas R$ 5,00", "Meio Amargo",
                "Cereja", 5.0);
    }

    private void adicionarBolo(String chave, String nome, String cobertura, String desc, String massa, String recheio,
            Double preco) {
        Bolo b = new Bolo();
        b.setNome(nome);
        b.setCobertura(cobertura);
        b.setDescricao(desc);
        b.setMassa(massa);
        b.setRecheio(recheio);
        b.setPrecoBase(preco);
        prototipos.put(chave, b);
    }

    private void adicionarBombom(String chave, String nome, String desc, String chocolate, String recheio,
            Double preco) {
        Bombom b = new Bombom();
        b.setNome(nome);
        b.setDescricao(desc);
        b.setTipoChocolate(chocolate);
        b.setRecheio(recheio);
        b.setPrecoBase(preco);
        prototipos.put(chave, b);
    }

    public Map<String, Map<String, String>> getCatalogo() {
        Map<String, Map<String, String>> catalogoInfo = new HashMap<>();

        for (Map.Entry<String, Produto> entry : prototipos.entrySet()) {
            Produto produto = entry.getValue();
            Map<String, String> info = new HashMap<>();

            info.put("nome", produto.getNome());
            info.put("descricao", produto.getDescricao());

            info.put("preco", String.valueOf(produto.getPrecoTotal(1)));

            catalogoInfo.put(entry.getKey(), info);
        }

        return catalogoInfo;
    }

    public Produto buscarPrototipo(String chave) {
        // retorna um clone do protótipo para evitar alterações no modelo original
        return prototipos.get(chave).clone();
    }
}