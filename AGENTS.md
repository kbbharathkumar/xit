# AGENTS.md
## AI Behavior & Code Generation Rules for Trading Platform

Version: 1.0  
Scope: Entire Repository  
Applies To: All AI Agents (Codex, GPT, Automation Tools)

---

# 1. SYSTEM IDENTITY (NON-NEGOTIABLE)

This repository implements a **modular algorithmic trading engine ecosystem**.

Core properties:

- Event-driven architecture
- Strict module separation
- Plugin-based extensibility
- Deterministic behavior

At the center of the system is:

> EXIT ENGINE (PRIMARY COMPONENT)

The exit engine is:

- Entry-agnostic
- Broker-agnostic
- Deterministic
- Capital-preservation focused

AI must treat the exit engine as the **most critical component**.

---

# 2. HARD INVARIANTS (MUST NEVER BE VIOLATED)

The following rules are ABSOLUTE.

AI must NEVER generate code that violates them.

## 2.1 Exit Engine Invariants

- Stop loss MUST NEVER decrease
- Exit logic MUST be deterministic
- Exit logic MUST NOT depend on entry logic
- Exit behavior MUST NOT change mid-trade
- All trades MUST be intraday (no overnight holding)
- Manual exit MUST always be allowed
- Capital protection MUST precede profit protection

## 2.2 Architectural Invariants

- No circular dependencies
- No cross-module violations
- No direct coupling between engines
- Communication MUST be event-driven

---

# 3. MODULE BOUNDARY RULES

Strict dependency direction must be preserved.

Allowed flow:

trading-domain
    ↑
indicators-engine
    ↑
strategy-composition
    ↑
entry-engine
    ↑
scanner-engine
    ↑
orchestrator-core
    ↓
exit-engine
    ↓
execution-infrastructure

Rules:

- Lower modules MUST NOT depend on higher modules
- Exit engine MUST NOT depend on entry engine
- Broker modules MUST NOT affect core engine logic

---

# 4. EXTENSIBILITY RULES

ALL extensions MUST follow registry pattern.

Allowed:

- IndicatorRegistry
- EntryStrategyRegistry
- ExitStrategyRegistry

AI must:

- REGISTER new components
- NEVER modify existing logic

FORBIDDEN:

- switch-case on type
- if-else chains for strategy selection
- hardcoded logic branches

---

# 5. EVENT-DRIVEN RULES

Modules MUST communicate ONLY via events.

Valid interaction:

- publish event
- subscribe to event

FORBIDDEN:

- direct method calls across modules
- shared mutable state between modules

---

# 6. DOMAIN MODEL RULES

Domain layer is PURE.

Rules:

- No business logic
- Immutable models preferred
- Only data + validation allowed

AI must NOT:

- add service logic to domain classes
- introduce dependencies in domain layer

---

# 7. INDICATOR RULES

Indicators MUST follow SDK architecture.

Rules:

- Algorithm MUST be stateless
- State MUST be separate
- Streaming indicators MUST be O(1)

FORBIDDEN:

- recalculating full history per tick
- mixing state and computation

---

# 8. ENTRY ENGINE RULES

- Entry engine ONLY generates signals
- Entry engine MUST NOT manage exits
- Entry engine MUST NOT contain risk logic

---

# 9. EXIT ENGINE RULES (CRITICAL)

Exit engine is STRICTLY controlled.

AI must ensure:

- Stop loss is monotonic (only tightens)
- Phase-based lifecycle is preserved
- Ownership logic is modular
- Hard safety is always enforced

Exit engine MUST:

- operate only on TradeContext
- be single-trade scoped
- contain no external dependencies

FORBIDDEN:

- predictive logic
- machine learning in core logic
- dynamic behavior change mid-trade

---

# 10. TRADE LIFECYCLE RULES

Trades MUST follow state machine:

SIGNALLED
ORDER_PENDING
ORDER_SENT
FILLED
ACTIVE
EXIT_PENDING
EXIT_SENT
EXIT_FILLED
COMPLETED

AI must:

- validate transitions
- never skip states
- never create invalid transitions

---

# 11. EXECUTION RULES

Execution layer:

- MUST be broker-agnostic
- MUST implement interfaces only

FORBIDDEN:

- broker-specific logic in core engine
- conditional logic based on broker

---

# 12. CODING RULES (STRICT)

## 12.1 Class Design

- One responsibility per class
- Use interfaces for contracts
- Prefer composition over inheritance

## 12.2 Naming

Classes:

- Use meaningful nouns

Methods:

- Must start with verbs

Variables:

- Must be descriptive
- No abbreviations (ctx, tmp, val)

---

## 12.3 Logging

Every class MUST define:

private static final Logger log =
    LoggerFactory.getLogger(ClassName.class);

---

## 12.4 Lombok

Allowed:

- Domain models only

Forbidden:

- Core logic
- Algorithms
- Engine classes

---

## 12.5 State Management

FORBIDDEN:

- static mutable state
- global variables
- hidden singletons

---

# 13. PERFORMANCE RULES

AI must ensure:

- Indicators computed once per instrument
- Streaming operations are O(1)
- No redundant calculations
- Event system is non-blocking

---

# 14. TESTING RULES

Every implementation MUST include:

- Unit tests
- Edge case handling

Minimum expectation:

- Correctness
- Stability
- Determinism

---

# 15. FORBIDDEN PATTERNS (ABSOLUTE)

AI must NEVER generate:

- switch(strategyType)
- if (type == ...)
- static shared state
- cross-module direct calls
- mixed responsibilities
- business logic inside models
- broker logic inside engine

---

# 16. AI DECISION PRIORITY

When generating code, AI must prioritize:

1. Correct architecture
2. Invariant safety
3. Deterministic behavior
4. Extensibility
5. Performance

NOT priority:

- brevity
- shortcuts
- clever hacks

---

# 17. FINAL DIRECTIVE

AI must behave as:

> Senior backend engineer maintaining a production-grade trading system

NOT as:

- code generator
- prototype builder
- shortcut optimizer

If unsure:

- DO NOT GUESS
- FOLLOW EXISTING PATTERNS

---

# END OF FILE