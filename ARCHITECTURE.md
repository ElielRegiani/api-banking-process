# Arquitetura Hexagonal - Banking Investment API

## 📐 Visão Geral

Este projeto implementa a **Arquitetura Hexagonal** (também conhecida como Arquitetura de Portas e Adaptadores), um padrão que desacopla o núcleo da aplicação de suas dependências externas.

## 🎯 Princípios Principais

1. **Isolamento do Domínio**: A lógica de negócio fica no centro, independente de frameworks e bibliotecas externas
2. **Portas**: Interfaces que definem os contratos de comunicação
3. **Adaptadores**: Implementações concretas que conectam a aplicação ao mundo externo

## 📁 Estrutura de Diretórios

```
src/main/kotlin/investiment/message/banking/process/
├── domain/                          # ❤️ Coração da aplicação (sem dependências externas)
│   ├── model/                       # Entidades de domínio
│   │   ├── StockSymbol.kt
│   │   ├── StockQuote.kt
│   │   ├── StockFundamentals.kt
│   │   └── InvestmentSyncEvent.kt
│   ├── ports/                       # Interfaces (contratos)
│   │   ├── inbound/                 # Portas de entrada (use cases)
│   │   │   └── SyncInvestmentsUseCase.kt
│   │   └── outbound/                # Portas de saída (dependências)
│   │       ├── InvestmentDataProvider.kt
│   │       └── EventPublisher.kt
│   └── exception/                   # Exceções de domínio
│       └── QuoteNoteFoundException.kt
│
├── app/                             # 🔄 Aplicação (orquestra o domínio)
│   └── services/
│       └── SyncInvestmentsService.kt  # Implementa SyncInvestmentsUseCase
│
└── infra/                           # 🔌 Infraestrutura (adaptadores)
    ├── adapters/
    │   ├── inbound/                 # Adaptadores de entrada
    │   │   └── InvestmentController.kt  # REST API (HTTP)
    │   └── outbound/                # Adaptadores de saída
    │       ├── FmpInvestmentAdapter.kt  # API FMP (HTTP Client)
    │       ├── FmpFeignClient.kt       # Feign client para FMP
    │       ├── FmpMapper.kt
    │       ├── KafkaEventPublisher.kt   # Kafka (Message Broker)
    │       └── dto/                     # Data Transfer Objects
    │           ├── FmpQuoteResponse.kt
    │           └── FmpKeyMetricsResponse.kt
    ├── config/                      # Configurações da infraestrutura
    │   ├── KafkaConfig.kt
    │   └── WebClientConfig.kt
    ├── scheduler/                   # Adaptadores de evento agendado
    │   └── DailyInvestmentScheduler.kt
    └── exception/                   # Handlers globais
        └── GlobalExceptionHandler.kt
```

## 🔄 Fluxo de Dados - Caso de Uso

### Sincronização Manual (HTTP)

```
┌─────────────┐
│   Cliente   │
│   (HTTP)    │
└──────┬──────┘
       │ POST /api/v1/investments/sync
       ▼
┌──────────────────────────────────────┐
│  InvestmentController (Adapter In)   │◄─── Porta Inbound
├──────────────────────────────────────┤
│   Traduz HTTP → SyncInvestmentsUseCase
└──────┬───────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────┐
│  SyncInvestmentsService (App Layer)  │
│  Implementa SyncInvestmentsUseCase   │
└──────┬───────────────────────────────┘
       │
       ├─────────────────────────────────┐
       │                                 │
       ▼                                 ▼
   ┌───────────────────┐        ┌──────────────────┐
   │  InvestmentData   │        │  EventPublisher  │
   │   Provider        │        │  (Porta Out)     │
   │  (Porta Out)      │        └────────┬─────────┘
   └────┬──────────────┘                 │
        │                                │
        ▼                                ▼
   ┌────────────────────────┐    ┌──────────────────────┐
   │ FmpInvestmentAdapter   │    │ KafkaEventPublisher  │
   │  (Adapter Out)         │    │  (Adapter Out)       │
   └────┬───────────────────┘    └──────┬───────────────┘
        │                               │
        ▼                               ▼
   ┌────────────────────────┐    ┌──────────────────────┐
   │  FMP REST API          │    │    Kafka Broker      │
   │ (External Service)     │    │ (External System)    │
   └────────────────────────┘    └──────────────────────┘
```

### Sincronização Agendada (Scheduler)

```
┌──────────────────────────────┐
│ DailyInvestmentScheduler     │◄─── Adapter Inbound
│ (Cron: 8:00 AM)              │    Triggered by Spring
├──────────────────────────────┤
│ Dispara SyncInvestmentsUseCase
└──────┬───────────────────────┘
       │
       └──► [Mesmo fluxo acima]
```

## 💼 Camadas e Responsabilidades

### 1. Domain Layer (Núcleo)
- **Sem dependências externas** (exceto stdlib)
- Contém lógica de negócio pura
- Define portas (interfaces)
- Independente de frameworks

**Arquivos:**
- `model/*` - Entidades de domínio
- `ports/*` - Interfaces de contrato
- `exception/*` - Exceções de negócio

### 2. Application Layer (Aplicação)
- Orquestra o domínio
- Implementa casos de uso (portas inbound)
- Coordena chamadas às dependências (portas outbound)
- Pode ter dependências da camada de infraestrutura

**Arquivos:**
- `app/services/*` - Implementações de use cases

### 3. Infrastructure Layer (Infraestrutura)
- Adaptadores concretos (implementações das portas)
- Integrações com ferramentas externas (APIs, BD, Message Brokers)
- Configurações do framework
- Controllers, Handlers, etc.

**Adaptadores Inbound:**
- REST Controllers
- Event Listeners
- Schedulers

**Adaptadores Outbound:**
- HTTP Clients (Feign)
- Database Repositories
- Message Brokers (Kafka)
- Cache Clients

## 🔗 Portas (Contracts)

### Portas Outbound (Dependências da Aplicação)

#### InvestmentDataProvider
```kotlin
interface InvestmentDataProvider {
    fun fetchQuote(symbol: StockSymbol): StockQuote
    fun fetchFundamentals(symbol: StockSymbol): StockFundamentals
}
```
**Implementação:** `FmpInvestmentAdapter`

#### EventPublisher
```kotlin
interface EventPublisher {
    fun publish(payload: Any, topic: String? = null)
}
```
**Implementação:** `KafkaEventPublisher`

### Portas Inbound (Use Cases)

#### SyncInvestmentsUseCase
```kotlin
interface SyncInvestmentsUseCase {
    fun sync(): Int
}
```
**Implementação:** `SyncInvestmentsService`

## 🎁 Adaptadores (Adapters)

### Inbound Adapters
Convertem requisições externas em chamadas ao domínio

- **InvestmentController** - REST API (HTTP)
- **DailyInvestmentScheduler** - Scheduled Tasks

### Outbound Adapters
Convertem solicitações do domínio em chamadas a sistemas externos

- **FmpInvestmentAdapter** - Integração com API FMP (via Feign)
- **KafkaEventPublisher** - Publicação em Kafka

## 🔄 Padrões Utilizados

### 1. Dependency Injection
Spring gerencia instâncias e injeção de dependências

### 2. Adapter Pattern
Adaptadores implementam portas, desacoplando domínio de implementações

### 3. Data Transfer Objects (DTOs)
`FmpQuoteResponse`, `FmpKeyMetricsResponse` - Estruturas do API externo

### 4. Mapper Pattern
`FmpMapper` - Converte DTOs → Domain Models

### 5. Circuit Breaker / Error Handling
Exceções customizadas, propagação controlada

## 📊 Fluxo de Dados Detalhado

### Entrada de Dados (Inbound)
```
HTTP Request 
    ↓
InvestmentController (REST Adapter)
    ↓
SyncInvestmentsUseCase (Port Interface)
    ↓
SyncInvestmentsService (Use Case Implementation)
```

### Saída de Dados (Outbound)
```
SyncInvestmentsService
    ↓
InvestmentDataProvider (Port)
    ↓
FmpInvestmentAdapter (Adapter)
    ↓
FmpFeignClient (HTTP Client)
    ↓
FMP REST API
```

### Publicação de Eventos (Outbound)
```
SyncInvestmentsService
    ↓
EventPublisher (Port)
    ↓
KafkaEventPublisher (Adapter)
    ↓
Kafka Topic
```

## 🧪 Testabilidade

A arquitetura hexagonal permite fácil testagem:

```kotlin
// Testes de Unidade - Mock das portas
@Test
fun testSyncInvestments() {
    // Mock InvestmentDataProvider
    // Mock EventPublisher
    // Testar SyncInvestmentsService
}

// Testes de Integração - Adapters reais
@SpringBootTest
class InvestmentControllerIntegrationTest {
    // Testar fluxo completo
}
```

## 🚀 Escalabilidade

Para adicionar novo adapter (ex: Nova API de dados):

1. **Criar novo adapter** implementando `InvestmentDataProvider`
2. **Registrar como Bean** no Spring
3. **Nenhuma mudança no domínio ou em outros adapters**

Exemplo:
```kotlin
@Component
class AlphaVantageAdapter : InvestmentDataProvider {
    // Nova implementação
}
```

## 📝 Configuração

Arquivo: `application.yaml`

```yaml
investment:
  symbols:
    - AAPL
    - MSFT
  kafka:
    topic: stock-investments

fmp:
  url: https://financialmodelingprep.com/api/v3
  api-key: ${FMP_API_KEY}
```

## 🛠️ Tratamento de Erros

Hierarquia de exceções:

```
InvestmentException (Base)
├── QuoteNotFoundException
├── FundamentalsNotFoundException
├── ExternalApiException
└── DataMappingException
```

## 📚 Referências

- [Alistair Cockburn - Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Framework - Dependency Injection](https://spring.io/projects/spring-framework)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
