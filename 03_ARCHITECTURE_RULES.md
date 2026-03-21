# ARCHITECTURE RULES
## Trading Platform Engine Ecosystem

Version: 1.0  
Purpose: Enforce strict architectural constraints and industry standards  
Audience: Codex, Developers, Architects  

---

# 1. CORE PRINCIPLE

The system must remain:

- Modular  
- Deterministic  
- Extensible  
- Broker-independent  
- Entry-independent  

Any change that violates these is not allowed.

---

# 2. SOLID PRINCIPLES (MANDATORY)

All code must strictly follow SOLID principles.

---

## 2.1 Single Responsibility Principle (SRP)

Each class must have exactly one responsibility.

### Correct Examples

- RSIIndicator → calculates RSI only  
- IndicatorRegistry → manages indicator registration  
- TradeStateMachine → handles state transitions  

### Forbidden

- Mixing multiple responsibilities in one class  
- Combining entry and exit logic  
- Combining calculation and execution logic  

---

## 2.2 Open/Closed Principle (OCP)

The system must be open for extension and closed for modification.

### Rules

- Do not modify existing core classes  
- Extend functionality using registries and factories  

### Correct

- Register new indicator via IndicatorRegistry  

### Forbidden

- Adding if-else chains for new types  
- Adding switch-case logic for extensibility  
- Modifying existing classes to add new features  

---

## 2.3 Dependency Inversion Principle (DIP)

High-level modules must depend on interfaces only.

### Allowed Interfaces

- Indicator  
- StreamingIndicator  
- EntryStrategy  
- ExitStrategy  
- MarketDataFeed  
- OrderExecutor  

### Forbidden

- Direct dependency on concrete implementations  
- Using broker-specific classes inside core logic  

---

# 3. MODULE BOUNDARY RULES

---

## 3.1 Dependency Order (STRICT)

Modules must follow this dependency order (lowest to highest):

1. trading-domain  
2. indicators-engine  
3. strategy-composition  
4. entry-engine  
5. scanner-engine  
6. orchestrator-core  
7. exit-engine  
8. execution-infrastructure  

---

## 3.2 Dependency Rules

- A module may depend only on modules above it in the list  
- A module must never depend on modules below it  
- Circular dependencies are strictly forbidden  

---

## 3.3 Critical Constraint

Exit Engine must NOT depend on:

- entry-engine  
- strategy-composition  
- scanner-engine  

Violation is strictly forbidden.

---

# 4. SEPARATION OF CONCERNS

Each module must handle only its defined responsibility.

---

## 4.1 Responsibility Mapping

| Concern                | Module                     |
|-----------------------|---------------------------|
| Indicator computation | indicators-engine         |
| Strategy definition   | strategy-composition      |
| Entry logic           | entry-engine              |
| Exit logic            | exit-engine               |
| Market data           | market-data-engine        |
| Execution             | execution-infrastructure  |
| Trade lifecycle       | trade-lifecycle           |

---

## 4.2 Forbidden Violations

- Exit logic inside entry engine  
- Indicator calculation inside strategy layer  
- Broker logic inside exit engine  
- Strategy logic inside scanner  

---

# 5. EVENT-DRIVEN RULES

---

## 5.1 Communication Model

All inter-module communication must occur through events.

---

## 5.2 Forbidden Patterns

- Direct method calls across modules  
- Shared mutable state  
- Tight coupling between modules  

---

## 5.3 Event Requirements

Events must be:

- Immutable  
- Self-contained  
- Serializable  

---

# 6. STATE MANAGEMENT RULES

---

## 6.1 TradeContext

- All trade state must exist inside TradeContext  
- No hidden state outside TradeContext  

---

## 6.2 Stateless Core

- No static mutable variables  
- No global shared state  
- Core engine must remain stateless  

---

## 6.3 Replayability

- TradeContext must be serializable  
- System must support event replay  

---

# 7. EXIT ENGINE RULES (CRITICAL)

---

## 7.1 Independence

Exit Engine must be:

- Entry-agnostic  
- Strategy-agnostic  
- Broker-agnostic  

---

## 7.2 Non-Negotiable Invariants

The following rules must never be violated:

- Stop loss must never decrease  
- Capital protection must precede profit protection  
- Exit logic must not change mid-trade  
- All trades must be intraday  
- Forced end-of-day exit must always execute  

---

## 7.3 Forbidden Behavior

- Using entry logic inside exit logic  
- Changing strategy during trade  
- Loosening stop loss  
- Introducing predictive or adaptive behavior  

---

# 8. REGISTRY PATTERN (MANDATORY)

---

## 8.1 Required Registries

- IndicatorRegistry  
- EntryStrategyRegistry  
- ExitStrategyRegistry  

---

## 8.2 Rules

- No switch-case extensibility  
- No if-else chains for type selection  
- Use metadata and factory pattern  

---

## 8.3 Example

```java
registry.register(
    new IndicatorDefinition(
        metadata,
        config -> new CustomIndicator(config)
    )
);

---

# 9. INDICATOR RULES

## 9.1 Execution Rules

- Indicators must be computed once per instrument  
- Results must be shared across strategies  

## 9.2 Performance Rules

- Streaming indicators must operate in O(1)  
- Avoid redundant recalculations  

## 9.3 Structure Rules

Separate:

- Algorithm (stateless)  
- State (memory objects)  

---

# 10. STRATEGY RULES

## 10.1 Structure

Strategies must be:

- Declarative  
- Built as condition trees  

## 10.2 Forbidden

- Hardcoded strategies  
- Mixing evaluation and execution  
- Embedding indicator logic inside strategies  

---

# 11. EXECUTION RULES

## 11.1 Broker Isolation

- Broker logic must exist only in execution-infrastructure  
- Core engine must not depend on broker implementations  

## 11.2 Adapter Pattern

Each broker must implement:

- MarketDataFeed  
- OrderExecutor  

---

# 12. CONFIGURATION RULES

## 12.1 Externalization

Configuration must be externalized via:

- YAML  
- Environment variables  
- API  
- Database  

## 12.2 Forbidden

- Hardcoded values  
- Magic numbers  

---

# 13. LOGGING RULES

## 13.1 Mandatory Logging

All modules must log:

- Key events  
- State transitions  
- Errors  

## 13.2 Structured Logging

Logs must include:

- tradeId  
- instrument  
- eventType  
- timestamp  

---

# 14. TESTABILITY RULES

## 14.1 Requirements

- All modules must be unit testable  
- No hidden dependencies  

## 14.2 Coverage

Minimum test coverage: 90%+

---

# 15. PERFORMANCE RULES

- No blocking operations in event flow  
- No redundant computations  
- Efficient memory usage  

---

# 16. FORBIDDEN ANTI-PATTERNS

The following are strictly forbidden:

- God classes  
- Static mutable state  
- Circular dependencies  
- Tight coupling  
- Business logic in domain models  
- Switch-case extensibility  
- Hidden side effects  

---

# 17. CODE REVIEW CHECKLIST

Every pull request must verify:

- SOLID compliance  
- No module boundary violations  
- Registry pattern usage  
- Tests included  
- Logging present  
- No forbidden patterns  

---

# 18. FINAL RULE

If any design:

- Breaks modularity  
- Breaks determinism  
- Breaks exit engine independence  

It must be rejected.