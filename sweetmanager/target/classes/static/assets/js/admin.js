const canalComunicacao = new BroadcastChannel("sweet_manager_updates");

canalComunicacao.onmessage = (evento) => {
  if (evento.data === "atualizar_pedidos") {
    console.log("Novo pedido detectado! Atualizando painel...");
    carregarPedidosAdmin();
  }
};

async function carregarInfoAdmin() {
  const response = await fetch("/usuarios/me");
  if (response.ok) {
    const user = await response.json();
    document.getElementById("welcome-msg").innerText =
      `Bem-vinda, ${user.nome} (ADM)`;
  }
}

async function carregarPedidosAdmin() {
  const response = await fetch("/pedidos/todos");
  const pedidos = await response.json();

  document.querySelectorAll(".card-pedido").forEach((card) => card.remove());

  pedidos.forEach((p) => {
    const statusId = p.status
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/\s/g, "");

    const col = document.getElementById("col-" + statusId);

    if (col) {
      const resumoItens =
        p.itens && p.itens.length > 0
          ? p.itens
              .map((item) => `${item.quantidade}x ${item.descricao}`)
              .join("; ")
          : "Sem detalhes";

      const dataObj = new Date(p.dataPedido);
      const dataFormatada = dataObj.toLocaleDateString("pt-BR");
      const horaFormatada = dataObj.toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });

      const card = document.createElement("div");
      card.className = "card-pedido";

      const podeAvancar = p.status !== "Entregue" && p.status !== "Cancelado";
      const podeCancelar = podeAvancar && p.status !== "Enviado";

      card.innerHTML = `
                <div class="pedido-topo">
                    <strong>#${p.id} - ${p.usuario.nome}</strong>
                    <small>📅 ${dataFormatada} às ${horaFormatada}</small>
                </div>
                
                <div class="pedido-itens">
                    <strong>Itens:</strong><br>
                    ${resumoItens}
                </div>

                <div class="pedido-total">
                    Total: R$ ${p.precoTotal.toFixed(2)}
                </div>

                <div class="acoes-card">
                    ${podeAvancar ? `<button class="btn-avancar" onclick="avancar(${p.id})">Avançar</button>` : ""}
                    ${podeCancelar ? `<button class="btn-cancelar" onclick="cancelar(${p.id})">Cancelar</button>` : ""}
                </div>
            `;
      col.appendChild(card);
    }
  });
}

async function avancar(id) {
  const response = await fetch(`/pedidos/${id}/avancar`, { method: "POST" });
  if (response.ok) {
    carregarPedidosAdmin();
    canalComunicacao.postMessage("atualizar_pedidos");
  }
}

async function cancelar(id) {
  const result = await Swal.fire({
    title: "Deseja realmente cancelar?",
    text: "Essa ação não poderá ser desfeita.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Sim, cancelar",
    cancelButtonText: "Voltar",
    confirmButtonColor: "#ff4d6d",
    cancelButtonColor: "#b0b0b0",
    background: "#fff0f5",
  });

  if (result.isConfirmed) {
    const response = await fetch(`/pedidos/${id}/cancelar`, { method: "POST" });

    if (response.ok) {
      await Swal.fire({
        title: "Pedido cancelado!",
        icon: "success",
        confirmButtonColor: "#ff4d6d",
        background: "#fff0f5",
      });

      carregarPedidosAdmin();
      canalComunicacao.postMessage("atualizar_pedidos");
    }
  }
}

async function cadastrarAdmin() {
  const nome = document.getElementById("novo-admin-nome").value;
  const email = document.getElementById("novo-admin-email").value;
  const senha = document.getElementById("novo-admin-senha").value;

  if (!nome || !email || !senha) {
    Swal.fire({
      title: "Campos obrigatórios",
      text: "Por favor, preencha todos os campos para o novo administrador.",
      icon: "warning",
      confirmButtonColor: "#ff4d6d",
      background: "#fff0f5",
    });
    return;
    return;
  }

  const novoAdmin = { nome, email, senha };

  try {
    const response = await fetch("/usuarios/cadastrar-admin", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(novoAdmin),
    });

    if (response.ok) {
      Swal.fire({
        title: "Administrador cadastrado!",
        text: `${nome} foi cadastrado com sucesso.`,
        icon: "success",
        confirmButtonColor: "#ff4d6d",
        background: "#fff0f5",
      });
      document.getElementById("novo-admin-nome").value = "";
      document.getElementById("novo-admin-email").value = "";
      document.getElementById("novo-admin-senha").value = "";
    } else {
      const erro = await response.text();
      Swal.fire({
        title: "Erro ao cadastrar",
        text: erro,
        icon: "error",
        confirmButtonColor: "#ff4d6d",
        background: "#fff0f5",
      });
    }
  } catch (error) {
    console.error("Erro na requisição:", error);
    Swal.fire({
      title: "Erro de conexão",
      text: "Não foi possível se comunicar com o servidor.",
      icon: "error",
      confirmButtonColor: "#ff4d6d",
      background: "#fff0f5",
    });
  }
}
const originalOnload = window.onload;
window.onload = async () => {
  await carregarInfoAdmin();
  await carregarPedidosAdmin();
};
