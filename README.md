# Sweet Manager 🍰

O **Sweet Manager** é um sistema de gerenciamento para uma confeitaria virtual, desenvolvido para facilitar o controle de produtos (bolos e bombons), rascunhos de pedidos e o fluxo de entrega. O projeto utiliza padrões de projeto robustos e uma arquitetura moderna em Java.

## 🚀 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 4.0.3**
* **Spring Data JPA** para persistência de dados
* **Spring Security** para autenticação e controle de acesso
* **SQL Server** como banco de dados
* **Docker** para conteinerização do banco de dados
* **Lombok** para redução de código boilerplate
* **Maven** para gerenciamento de dependências

## 🛠️ Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:
* JDK 21
* Docker e Docker Compose
* Maven (opcional, caso use o `mvnw` incluso)

## 🏗️ Instruções de Execução

### 1. Configurar o Banco de Dados
O projeto utiliza o SQL Server 2022 via Docker. Para subir a instância do banco:

1. Abra o terminal na raiz do projeto.
2. Execute o comando:
   ```bash
   docker-compose up -d
   ```
   *Isso iniciará um container chamado `m47su-db` na porta `1433`*.

### 2. Configurar a Aplicação
As configurações de conexão já estão pré-definidas no arquivo `src/main/resources/application.properties`:
* **URL:** `jdbc:sqlserver://localhost:1433;databaseName=ConfeitariaDB`
* **Usuário:** `sa`
* **Senha:** `SenhaForte123!`

### 3. Executar o Projeto
Com o banco de dados ativo, execute a aplicação utilizando o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## 👥 Usuários para Teste

Para validar as diferentes permissões de acesso (Usuário Comum vs. Administrador), utilize as credenciais abaixo:

### **Usuário Comum**
* **Nome:** Teste
* **E-mail:** `teste@teste.com`
* **Senha:** `123`

### **Usuário Administrador (ADM)**
* **Nome:** Admin
* **E-mail:** `admin@sweet`
* **Senha:** `admin123`

---

## 📂 Estrutura de Padrões de Projeto
O sistema demonstra a aplicação prática de diversos Design Patterns:
* **Prototype:** No registro e clonagem de produtos.
* **State:** No gerenciamento do ciclo de vida dos pedidos.
* **Bridge:** Na separação entre o pedido e a logística de entrega.
* **Decorator:** Para adição dinâmica de customizações em produtos.
* **Memento:** Para salvar e restaurar estados de produtos/rascunhos.

---

## 🧪 Guia de Testes e Regras de Negócio

Este guia detalha como navegar pelas funcionalidades do **Sweet Manager** e quais regras automáticas o sistema aplica em cada ação.

### 1. Fluxo do Cliente (Customização e Pedidos)

* **Seleção de Produtos:**
    * **Bolos:** Você pode escolher um bolo base (ex: Chocolate) e optar por manter o recheio padrão ou alterá-lo. O sistema utiliza o padrão **Prototype** para clonar a base e permitir a modificação.
    * **Bombons:** Você pode escolher um bombom base (ex: Cereja) e optar por manter o chocolate padrão ou alterá-lo. Assim como os bolos, cada bombom é gerado a partir de um protótipo pré-configurado.
* **Regras de Customização (Pattern Decorator):**
    * **Topo de Bolo:** Ao adicionar um topo, o sistema identifica a categoria: cobrado **R$ 5,00** para bolos e **R$ 1,00** para bombons.
    * **Embalagem de Presente:**
        * Para **Bolos**, a cobrança é **unitária** (por item).
        * Para **Bombons**, a embalagem não é unitária, permitindo agrupar vários itens. **Exceção:** Se os bombons forem de sabores diferentes, o sistema permite a cobrança diferenciada para garantir a separação adequada. Ambas as embalagens custam **R$ 8,50**.
* **Seleção de Entrega:**
    * **Delivery:** Você pode escolher a entrega por meio de delivery, custando uma taxa adicional de **R$ 15,00**.
    * **Retirada:** Você pode escolher a entrega por da retirada, sendo gratuita.
* **Rascunhos:** É possível salvar como rascunho uma costumização feita clicando no botão "Salvar Rascunho" no final do formulário e retomar após clicar no botão "Restaurar Anterior", mostrando sua costumização anterior no formulário.
* **Acompanhamento:** Na seção **"Meus Pedidos"**, o cliente visualiza o estado atual do seu pedido e o logístico de entrega definido.

### 2. Fluxo de Administração (Gestão de Estados)

* **Painel Admin:** O administrador tem acesso a todos os pedidos e pode interagir diretamente com o ciclo de vida de cada um.
* **Avanço de Status:** Ao clicar em **"Avançar"**, o pedido segue o fluxo lógico: *Recebido* ➔ *Em Preparação* ➔ *Enviado* ➔ *Entregue*.
* **Cancelamento e Restrições:** O botão **"Cancelar"** interrompe o pedido.
    * **Regra Crítica:** O sistema impede o cancelamento de pedidos que já possuem o status **"Enviado"**, lançando uma mensagem de erro ao tentar realizar a ação.

---

## 🛠️ Principais Métodos do Sistema

Abaixo estão listados os métodos fundamentais que regem a inteligência do **Sweet Manager**:

### Gestão de Produtos (`ProdutoRegistry` & `Produto`)
* `buscarPrototipo(String chave)`: Localiza e retorna um clone de um produto (Bolo/Bombom) para edição segura.
* `aplicarCustomizacoes(Map<String, Object> dados)`: Método abstrato que permite aos Decorators (Topo, Embalagem) modificar os atributos e preços do produto original.
* `getCatalogo()`: Retorna a lista completa de produtos disponíveis para a interface do usuário.

### Lógica de Pedidos (`Pedido`)
* `avancarEstado()`: Executa a transição automática para o próximo estágio do pedido com base no padrão State.
* `cancelarPedido()`: Altera o estado para "Cancelado", validando se o item já não foi despachado.
* `obterLogistica()`: Utiliza o padrão Bridge para retornar a string de processamento da entrega (ex: "Saindo para entrega" ou "Aguardando retirada").
* `reconstruirPadroes()`: Método invocado após o carregamento do banco (`@PostLoad`) para restaurar os objetos de Estado e Estratégia de Entrega.

### Persistência e Recuperação
* `salvarRascunho()`: (Em `RascunhoService`) Utiliza o **Memento** para capturar o estado de um produto customizado e salvá-lo para finalização posterior.
* `buscarPedidosPorUsuario(Usuario usuario)`: Recupera o histórico filtrado para a visão do cliente.
