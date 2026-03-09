# Banking Investment Process API

API que sincroniza dados de investimentos (ações) de uma API pública e publica eventos em Kafka.

## 📋 Pré-requisitos

- Java 21+
- Gradle 8.0+
- Kotlin 1.9.25+
- Kafka (local ou remoto)
- Spring Boot 3.5.7+

## 🏗️ Arquitetura

Este projeto implementa a **Arquitetura Hexagonal** com:

- **Domain Layer**: Lógica de negócio pura (sem dependências externas)
- **Application Layer**: Orquestração de casos de uso
- **Infrastructure Layer**: Adaptadores para sistemas externos

Consulte [ARCHITECTURE.md](./ARCHITECTURE.md) para detalhes completos.

## 🚀 Como Executar

### 1. Clonar o projeto

```bash
cd banking.process
```

### 2. Configurar variáveis de ambiente

```bash
export FMP_API_KEY=your-api-key-here
```

Ou criar arquivo `.env`:
```
FMP_API_KEY=your-api-key-here
```

### 3. Iniciar Kafka (Docker)

```bash
docker-compose up -d
```

Ou manualmente:
```bash
# Terminal 1 - Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2 - Kafka
bin/kafka-server-start.sh config/server.properties
```

### 4. Build e Run

```bash
# Build
./gradlew build

# Run
./gradlew bootRun
```

A API estará disponível em `http://localhost:8080`

## 📡 API Endpoints

### Sincronização Manual

```bash
POST /api/v1/investments/sync

Response:
{
  "success": true,
  "message": "Synchronization completed successfully",
  "processedSymbols": 2
}
```

## ⏰ Sincronização Automática

A API executa sincronização automática todos os dias às **8:00 AM** (cron: `0 0 8 * * *`)

Você pode configurar em `application.yaml`:

```yaml
investment:
  symbols:
    - AAPL
    - MSFT
    - GOOG
```

## 🧪 Testes

```bash
# Executar todos os testes
./gradlew test

# Executar testes com relatório
./gradlew test --info

# Executar teste específico
./gradlew test --tests FmpInvestmentAdapterTest
```

### Cobertura de Testes

- ✅ Domain Models
- ✅ Application Services
- ✅ Infrastructure Adapters
- ✅ REST Controllers
- ✅ Exception Handling
- ✅ Data Mapping

## 🔧 Configuração

### application.yaml

```yaml
spring:
  application:
    name: bankingprocess
  kafka:
    bootstrap-servers: localhost:9092
  task:
    scheduling:
      pool:
        size: 2

fmp:
  url: https://financialmodelingprep.com/api/v3
  api-key: ${FMP_API_KEY:demo}

investment:
  symbols:
    - AAPL
    - MSFT
    - GOOG
    - TSLA
  kafka:
    topic: stock-investments
    partition-count: 1
    replication-factor: 1

logging:
  level:
    investiment.message.banking: DEBUG
```

## 📊 Fluxo de Dados

```
Manual Trigger (HTTP)        Scheduled Trigger (Cron 8 AM)
        ↓                              ↓
┌──────────────────────┐     ┌─────────────────────────┐
│ InvestmentController │     │ DailyInvestmentScheduler│
└──────┬───────────────┘     └────────┬────────────────┘
       │                             │
       └─────────────┬───────────────┘
                     ▼
      ┌──────────────────────────────┐
      │ SyncInvestmentsService       │
      │ (Use Case Implementation)    │
      └──────┬─────────────┬─────────┘
             │             │
             ▼             ▼
      ┌─────────────┐ ┌─────────────────┐
      │ FMP API     │ │ Kafka Publisher │
      │ (Data In)   │ │ (Events Out)    │
      └─────────────┘ └─────────────────┘
```

## 🔌 Portas e Adaptadores

### Portas Outbound

1. **InvestmentDataProvider** → `FmpInvestmentAdapter`
   - Busca dados de investimentos da API FMP

2. **EventPublisher** → `KafkaEventPublisher`
   - Publica eventos em Kafka

### Adaptadores Inbound

1. **REST API** → `InvestmentController`
   - HTTP POST para sincronizar dados

2. **Scheduler** → `DailyInvestmentScheduler`
   - Dispara sincronização diária

## 🛠️ Estrutura de Exceções

```
InvestmentException (Base)
├── QuoteNotFoundException      # Ação não encontrada
├── FundamentalsNotFoundException # Fundamentos não encontrados
├── ExternalApiException        # Erro na API externa
└── DataMappingException        # Erro ao mapear dados
```

## 📝 Logging

O projeto usa SLF4J com configuração por nível:

```yaml
logging:
  level:
    investiment.message.banking: DEBUG
    org.springframework.web: INFO
    org.springframework.kafka: DEBUG
```

## 🚨 Tratamento de Erros

### Global Exception Handler

- `InvestmentException` → HTTP 400 (Bad Request)
- Exceções genéricas → HTTP 500 (Internal Server Error)
- Todos os erros retornam:
  ```json
  {
    "errorId": "uuid",
    "message": "Error message",
    "timestamp": "2024-03-01T10:30:00",
    "status": 400
  }
  ```

## 📦 Dependências Principais

- **Spring Boot 3.5.7** - Framework web
- **Spring Cloud OpenFeign** - HTTP Client
- **Spring Kafka** - Message Broker
- **Kotlin 1.9.25** - Linguagem
- **Jackson** - JSON serialization

## 🤝 Adicionar Novo Adapter

Para adicionar uma nova fonte de dados (ex: Alpha Vantage):

1. Criar novo adapter implementando `InvestmentDataProvider`
2. Registrar como `@Component` no Spring
3. Nenhuma mudança no domínio necessária

```kotlin
@Component
class AlphaVantageAdapter : InvestmentDataProvider {
    override fun fetchQuote(symbol: StockSymbol): StockQuote { ... }
    override fun fetchFundamentals(symbol: StockSymbol): StockFundamentals { ... }
}
```

## 📚 Referências

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
- [FMP API Documentation](https://financialmodelingprep.com/)

## 📄 Licença

MIT License

## 👤 Autor

Engenheiro de Software Senior

## 🤝 Contribuições

Pull requests são bem-vindos! Para mudanças significativas, abra uma issue primeiro para discutir as mudanças propostas.
