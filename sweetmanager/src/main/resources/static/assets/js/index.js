const canalComunicacao = new BroadcastChannel("sweet_manager_updates");

canalComunicacao.onmessage = (evento) => {
  if (evento.data === "atualizar_pedidos") {
    console.log("Atualizando pedidos por comando do ADM...");
    carregarMeusPedidos();
  }
};

async function carregarInfoUsuario() {
  const response = await fetch("/usuarios/me");
  if (response.ok) {
    const user = await response.json();
    document.getElementById("welcome-msg").innerText = `Olá, ${user.nome}!`;
  }
}

async function salvarEstadoAtual() {
  const tipo = document.getElementById("tipoDoce").value;
  const estado = {
    tipoDocura: tipo,
    base:
      tipo === "BOLO"
        ? document.getElementById("baseBolo").value
        : document.getElementById("baseBombom").value,
    recheio:
      tipo === "BOLO" ? document.getElementById("recheioBolo").value : null,
    chocolate:
      tipo === "BOMBOM"
        ? document.getElementById("chocolateBombom").value
        : null,
    quantidade: parseInt(document.getElementById("quantidade").value),
    comEmbalagem: document.querySelector("input.embalagem").checked,
    adicionais: Array.from(
      document.querySelectorAll(".topo-opcao:checked"),
    ).map((cb) => cb.value),
  };

  const response = await fetch("/pedidos/rascunho/salvar", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(estado),
  });
  if (response.ok) alert("Rascunho completo guardado!");
}

async function restaurarUltimoEstado() {
  const response = await fetch("/pedidos/rascunho/restaurar");
  if (response.ok) {
    const memento = await response.json();
    if (!memento) return;

    document.getElementById("tipoDoce").value = memento.categoriaUI;
    alternarCamposEspecificos();

    if (memento.categoriaUI === "BOLO") {
      document.getElementById("baseBolo").value = memento.baseChave;
      document.getElementById("recheioBolo").value = memento.recheio;
    } else {
      document.getElementById("baseBombom").value = memento.baseChave;
      document.getElementById("chocolateBombom").value = memento.tipoChocolate;
    }

    document.getElementById("quantidade").value = memento.quantidade || 1;
    document.querySelector("input.embalagem").checked =
      memento.adicionaisChaves.includes("EMBALAGEM");

    document.querySelectorAll(".topo-opcao").forEach((cb) => {
      cb.checked = memento.adicionaisChaves.includes(cb.value);
    });

    atualizarDescricao();
    alert("Rascunho restaurado!");
  }
}

let itensPedido = [];
let catalogoGlobal = {};

window.addEventListener("DOMContentLoaded", async () => {
  await carregarInfoUsuario();
  await carregarCatalogo();
  await carregarMeusPedidos();

  document
    .getElementById("baseBolo")
    .addEventListener("change", atualizarDescricao);
  document
    .getElementById("baseBombom")
    .addEventListener("change", atualizarDescricao);
  document
    .getElementById("tipoDoce")
    .addEventListener("change", alternarCamposEspecificos);
});

async function carregarCatalogo() {
  try {
    const response = await fetch("/pedidos/catalogo");
    if (response.ok) {
      catalogoGlobal = await response.json();
      popularSelects();
      atualizarDescricao();
    }
  } catch (error) {
    console.error("Erro ao carregar catálogo:", error);
  }
}

function popularSelects() {
  const selectBolo = document.getElementById("baseBolo");
  const selectBombom = document.getElementById("baseBombom");

  selectBolo.innerHTML = "";
  selectBombom.innerHTML = "";

  Object.keys(catalogoGlobal).forEach((chave) => {
    const item = catalogoGlobal[chave];
    const option = `<option value="${chave}">${item.nome}</option>`;

    if (chave.startsWith("BOLO")) {
      selectBolo.innerHTML += option;
    } else if (chave.startsWith("BOMBOM")) {
      selectBombom.innerHTML += option;
    }
  });
}

function atualizarDescricao() {
  const tipoDoceElement = document.getElementById("tipoDoce");
  if (!tipoDoceElement) return;

  const tipo = tipoDoceElement.value; // "BOLO" ou "BOMBOM"
  const valorTopo = tipo === "BOMBOM" ? 1.0 : 5.0;

  const tituloTopos = document.getElementById("titulo-topos");
  if (tituloTopos) {
    tituloTopos.innerText = `Topos Adicionais (+R$ ${valorTopo.toFixed(2)} cada):`;
  }

  const idBase = tipo === "BOLO" ? "baseBolo" : "baseBombom";
  const baseElement = document.getElementById(idBase);
  const chave = baseElement ? baseElement.value : null;

  const display = document.getElementById("info-produto");
  if (chave && typeof catalogoGlobal !== "undefined" && catalogoGlobal[chave]) {
    const info = catalogoGlobal[chave];
    display.innerHTML = `<strong>${info.nome}:</strong> ${info.descricao}`;
  }
}

function alternarCamposEspecificos() {
  const tipo = document.getElementById("tipoDoce").value;
  document.getElementById("camposBolo").style.display =
    tipo === "BOLO" ? "block" : "none";
  document.getElementById("camposBombom").style.display =
    tipo === "BOMBOM" ? "block" : "none";
  atualizarDescricao();
}

function adicionarAoCarrinho() {
  const tipo = document.getElementById("tipoDoce").value;
  const qtd = parseInt(document.getElementById("quantidade").value);

  if (isNaN(qtd) || qtd < 1) {
    alert("Por favor, insira uma quantidade válida.");
    return;
  }

  const item = {
    tipoDocura: tipo,
    quantidade: qtd,
    adicionais: [],
  };

  if (tipo === "BOLO") {
    item.base = document.getElementById("baseBolo").value;
    item.recheio = document.getElementById("recheioBolo").value;
  } else {
    item.base = document.getElementById("baseBombom").value;
    item.chocolate = document.getElementById("chocolateBombom").value;
  }

  document.querySelectorAll(".topo-opcao:checked").forEach((cb) => {
    item.adicionais.push({ tipo: "TOPO", escolha: cb.value });
    cb.checked = false;
  });

  const embalagemCheckbox = document.querySelector(".embalagem:checked");
  if (embalagemCheckbox) {
    item.adicionais.push({ tipo: "EMBALAGEM", escolha: "SIM" });
    embalagemCheckbox.checked = false;
  }

  itensPedido.push(item);
  renderizarCarrinho();
}

function renderizarCarrinho() {
  const lista = document.getElementById("lista-carrinho");
  const totalElement = document.getElementById("total-carrinho");
  
  let totalDaCompra = 0;

  if (itensPedido.length === 0) {
    lista.innerHTML = '<p class="empty-msg">Nenhum item adicionado ainda.</p>';
    if(totalElement) totalElement.innerHTML = "Total: R$ 0.00";
    return;
  }

  lista.innerHTML = itensPedido
    .map((it, idx) => {
      const info = catalogoGlobal[it.base] || {};
      const nomeBase = info.nome || it.base;
      
      let precoUnitario = parseFloat(info.preco || 0);
      let valorEmbalagem = 0;

      it.adicionais.forEach(adc => {
         if (adc.tipo === "TOPO") {
            const valorTopo = it.tipoDocura === "BOMBOM" ? 1.0 : 5.0;
            precoUnitario += valorTopo; 
         } else if (adc.tipo === "EMBALAGEM") {
            if (it.tipoDocura === "BOLO") {
                valorEmbalagem += 8.50 * it.quantidade; 
            } else if (it.tipoDocura === "BOMBOM") {
                valorEmbalagem += 8.50; 
            }
         }
      });

      let subtotalItem = (precoUnitario * it.quantidade) + valorEmbalagem;
      totalDaCompra += subtotalItem;

      return `
            <li class="cart-item">
                <div>
                    <span>${it.quantidade}x</span> ${nomeBase} 
                    <small style="display:block; color:#777">(${it.tipoDocura === "BOLO" ? it.recheio : it.chocolate})</small>
                    <small style="display:block; font-weight:bold; color:#555">R$ ${subtotalItem.toFixed(2)}</small>
                </div>
                <button onclick="removerDoCarrinho(${idx})" style="background:none; color:#e74c3c; font-size:1.2rem; cursor:pointer">✕</button>
            </li>
        `;
    })
    .join("");

    const opcaoEntrega = document.querySelector('input[name="entrega"]:checked');
    if (opcaoEntrega && opcaoEntrega.value === "DELIVERY") {
        totalDaCompra += 15.0;
    }

    if(totalElement) {
        totalElement.innerHTML = `Total: R$ ${totalDaCompra.toFixed(2)}`;
    }
}

function removerDoCarrinho(index) {
  itensPedido.splice(index, 1);
  renderizarCarrinho();
}

async function finalizarPedido() {
  if (itensPedido.length === 0) {
    alert("Adicione pelo menos um item ao seu carrinho!");
    return;
  }

  const tipoEntrega = document.querySelector(
    'input[name="entrega"]:checked',
  ).value;

  const payload = {
    itens: itensPedido,
    tipoEntrega: tipoEntrega,
  };

  try {
    const response = await fetch("/pedidos/montar", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      alert("✨ Pedido Finalizado! Estamos preparando sua doçura.");
      itensPedido = [];
      renderizarCarrinho();
      carregarMeusPedidos();
      canalComunicacao.postMessage("atualizar_pedidos");
    } else {
      alert("Ocorreu um erro ao processar seu pedido.");
    }
  } catch (error) {
    console.error("Erro ao finalizar pedido:", error);
  }
}

async function cancelarPedido(idPedido) {
    if (!confirm(`Tem certeza que deseja cancelar o pedido #${idPedido}?`)) {
        return;
    }

    try {
        const response = await fetch(`/pedidos/${idPedido}/cancelar`, {
            method: 'POST'
        });

        if (response.ok) {
            alert("Pedido cancelado com sucesso!");
            carregarMeusPedidos(); 
            canalComunicacao.postMessage("atualizar_pedidos"); 
        } else {
            alert("Não foi possível cancelar. O pedido pode já ter sido enviado.");
        }
    } catch (error) {
        console.error("Erro ao cancelar o pedido:", error);
        alert("Ocorreu um erro ao comunicar com o servidor.");
    }
}

async function carregarMeusPedidos() {
  const listaElement = document.getElementById("lista-pedidos");
  try {
    const response = await fetch("/pedidos/meus-pedidos");
    if (response.ok) {
      const pedidos = await response.json();

      if (pedidos.length === 0) {
        listaElement.innerHTML = '<p class="empty-msg">Você ainda não tem pedidos.</p>';
        return;
      }

      listaElement.innerHTML = pedidos
        .reverse()
        .map((p) => {
          const resumoItens = p.itens && p.itens.length > 0
              ? p.itens.map((item) => `${item.quantidade}x ${item.descricao}`).join("; ")
              : "Sem detalhes disponíveis";

          const statusLimpo = p.status ? p.status.trim().toUpperCase() : "";

          let btnCancelar = "";
          if (statusLimpo !== "ENVIADO" && statusLimpo !== "ENTREGUE" && statusLimpo !== "CANCELADO") {
              btnCancelar = `
                <button onclick="cancelarPedido(${p.id})" 
                        style="margin-top: 10px; background: none; border: 1px solid #e74c3c; color: #e74c3c; padding: 4px 8px; border-radius: var(--radius-pill); cursor: pointer; width: 15%;">
                    Cancelar Pedido
                </button>`;
          }

          return `
                <div class="pedido-card">
                    <strong>Pedido #${p.id}</strong>
                    <div style="font-size: 0.85rem; margin: 5px 0; color: #555;">${resumoItens}</div>
                    <div style="font-weight: 600; color: var(--primary)">Total: R$ ${p.precoTotal.toFixed(2)}</div>
                    <div class="status-badge">${p.status}</div>
                    ${btnCancelar}
                </div>
                `;
        })
        .join("");
    }
  } catch (error) {
    console.error("Erro ao carregar pedidos:", error);
    listaElement.innerHTML = '<p class="empty-msg">Erro ao carregar pedidos.</p>';
  }
}
