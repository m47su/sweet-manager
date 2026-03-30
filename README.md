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
* Docker e Docker Compose (Certifique-se de que a sua máquina está habilitada para Virtualização)
* Maven (opcional, caso use o `mvnw` incluso)

## ✅ Recomendações

Instale a extensão "SQL Server (mssql)" caso utilize o Visual Studio Code:
<img width="983" height="348" alt="screenshot" src="https://github.com/user-attachments/assets/ffcc40e5-eeb8-4edd-ba4d-ef19262a8e2b" />

## 🏗️ Instruções de Execução

Siga os passos abaixo para rodar a aplicação localmente sem a necessidade de configurações manuais de SQL.

### 1. Subir o Banco de Dados (Docker)
A criação do banco de dados `ConfeitariaDB` é totalmente automatizada via Docker.
1. Certifique-se de que o Docker Desktop está aberto.
2. Certifique-se de estar na pasta raíz do projeto:
  ```powershell
  cd sweetmanager
  ```
3. No terminal, na pasta raiz do projeto, execute:
   ```bash
   docker-compose up -d
   ```
   *Isso iniciará o servidor SQL Server e criará a base de dados automaticamente.*

### 2. Executar a Aplicação
Utilize o Maven Wrapper incluído. Escolha o comando baseado no seu terminal:

* **Windows (PowerShell ou CMD):**
  ```powershell
  .\mvnw spring-boot:run
  ```
* **Linux ou WSL (Terminal/Bash):**
  ```bash
  chmod +x mvnw && ./mvnw spring-boot:run
  ```
* **Caso algum problema com o maven wrapper ocorra, utilize o comando:**
  ```powershell
  \apache-maven-3.9.14\bin\mvn.cmd spring-boot:run
  ```

A aplicação estará disponível em **`http://localhost:8081`**.

## 👥 Usuários para Teste (Data Seeding)

Ao iniciar a aplicação, o sistema cria automaticamente as tabelas e os usuários de teste abaixo, caso ainda não existam no banco:

| Perfil | E-mail | Senha |
| :--- | :--- | :--- |
| **Administrador** | `admin@sweet` | `admin123` |
| **Usuário Comum** | `teste@teste.com` | `123` |

---

## 📂 Estrutura de Padrões de Projeto
O sistema demonstra a aplicação prática de diversos Design Patterns:
* **Prototype:** Registro e clonagem de produtos (Bolos e Bombons).
* **State:** Gerenciamento do ciclo de vida dos pedidos (Recebido ➔ Preparação ➔ Enviado ➔ Entregue).
* **Bridge:** Separação entre a lógica do pedido e a logística de entrega (Delivery ou Retirada).
* **Decorator:** Adição dinâmica de customizações como Topo de Bolo e Embalagens.
* **Memento:** Sistema de rascunhos para salvar e restaurar estados de customização.

---

## 🛠️ Observações Técnicas

* **Porta do Servidor:** Alterada para **8081** para evitar conflitos com outros serviços que utilizam a porta padrão 8080.
* **Persistência:** O projeto utiliza `spring.jpa.hibernate.ddl-auto=update` para garantir que o esquema do banco esteja sempre sincronizado com as entidades Java.

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
