const canalComunicacao = new BroadcastChannel('sweet_manager_updates');

canalComunicacao.onmessage = (evento) => {
    if (evento.data === 'atualizar_pedidos') {
        console.log("Novo pedido detectado! Atualizando painel...");
        carregarPedidosAdmin(); 
    }
};

async function carregarInfoAdmin() {
    const response = await fetch("/usuarios/me");
    if (response.ok) {
        const user = await response.json();
        document.getElementById("welcome-msg").innerText = `Bem-vinda, ${user.nome} (ADM)`;
    }
}

async function carregarPedidosAdmin() {
    const response = await fetch("/pedidos/todos");
    const pedidos = await response.json();

    document.querySelectorAll('.card-pedido').forEach(card => card.remove());

    pedidos.forEach((p) => {
        const statusId = p.status
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .replace(/\s/g, "");

        const col = document.getElementById("col-" + statusId);

        if (col) {
            const resumoItens = p.itens && p.itens.length > 0 
                ? p.itens.map(item => `${item.quantidade}x ${item.descricao}`).join("; ")
                : "Sem detalhes";

            const dataObj = new Date(p.dataPedido);
            const dataFormatada = dataObj.toLocaleDateString('pt-BR');
            const horaFormatada = dataObj.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

            const card = document.createElement("div");
            card.className = "card-pedido";
            
            const podeAvancar = p.status !== "Entregue" && p.status !== "Cancelado";
            const podeCancelar = podeAvancar && p.status !== "Enviado";

            card.innerHTML = `
                <div style="border-bottom: 1px solid #eee; margin-bottom: 8px; padding-bottom: 5px;">
                    <strong>#${p.id} - ${p.usuario.nome}</strong><br>
                    <small style="color: #666;">📅 ${dataFormatada} às ${horaFormatada}</small>
                </div>
                
                <div style="font-size: 0.9rem; color: #444; margin-bottom: 10px;">
                    <strong>Itens:</strong><br>
                    ${resumoItens}
                </div>

                <div style="font-weight: bold; color: #ff4d8d; margin-bottom: 10px;">
                    Total: R$ ${p.precoTotal.toFixed(2)}
                </div>

                <div class="acoes-card">
                    ${podeAvancar ? `<button class="btn-avancar" onclick="avancar(${p.id})">Avançar ➔</button>` : ''}
                    ${podeCancelar ? `<button class="btn-cancelar" onclick="cancelar(${p.id})">Cancelar ✖</button>` : ''}
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
        canalComunicacao.postMessage('atualizar_pedidos');
    }
}

async function cancelar(id) {
    if (confirm("Deseja realmente cancelar?")) {
        const response = await fetch(`/pedidos/${id}/cancelar`, { method: "POST" });
        if (response.ok) {
            carregarPedidosAdmin(); 
            canalComunicacao.postMessage('atualizar_pedidos');
        }
    }
}

async function cadastrarAdmin() {
    const nome = document.getElementById("novo-admin-nome").value;
    const email = document.getElementById("novo-admin-email").value;
    const senha = document.getElementById("novo-admin-senha").value;

    if (!nome || !email || !senha) {
        alert("Por favor, preencha todos os campos para o novo administrador.");
        return;
    }

    const novoAdmin = { nome, email, senha };

    try {
        const response = await fetch("/usuarios/cadastrar-admin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(novoAdmin)
    });

        if (response.ok) {
            alert(`✅ Administrador ${nome} cadastrado com sucesso!`);
            document.getElementById("novo-admin-nome").value = "";
            document.getElementById("novo-admin-email").value = "";
            document.getElementById("novo-admin-senha").value = "";
        } else {
            const erro = await response.text();
            alert("Erro ao cadastrar: " + erro);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Erro de conexão com o servidor.");
    }
}
const originalOnload = window.onload;
window.onload = async () => {
    await carregarInfoAdmin();     
    await carregarPedidosAdmin(); 
};
