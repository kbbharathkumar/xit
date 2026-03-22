# MVP 1.0 Sprint Plan

## Purpose

This document defines the recommended **MVP 1.0 implementation plan** for the trading platform based on the repository vision and architecture documents.

The MVP is designed to be:

- executable end-to-end
- deterministic
- event-driven
- modular
- simulation-first
- centered on a safe and testable exit engine

The MVP deliberately avoids premature complexity such as live broker integrations, advanced scanner scaling, external plugin loading, distributed processing, or framework-heavy bootstrapping.

---

# 1. MVP Objective

The primary objective of MVP 1.0 is to deliver a **fully runnable vertical slice** of the platform that proves the architecture and validates the most critical business invariant:

> the exit engine must manage active trades safely, deterministically, and independently from entry logic.

The MVP should support the following end-to-end flow:

1. read deterministic sample market data
2. normalize and aggregate data into candles
3. compute a minimal set of indicators
4. evaluate one entry strategy
5. generate an entry signal
6. create a trade and move it through lifecycle states
7. simulate order execution
8. manage exits through phase-based stop-loss protection
9. force an end-of-day exit if needed
10. complete the trade lifecycle successfully

This gives the project a working core without violating architecture or overbuilding optional features.

---

# 2. MVP Scope

## 2.1 In Scope

The following capabilities are included in MVP 1.0:

- parent Maven project with multiple child modules
- pure Java implementation for core engines
- immutable domain models and immutable event payloads
- in-process event bus
- deterministic event ordering
- sample historical/simulated market feed
- 1-minute candle aggregation
- indicator engine with minimal streaming support
- one entry strategy implemented through registry-backed architecture
- trade lifecycle state machine
- simulated execution infrastructure
- exit engine with monotonic stop-loss behavior
- time-based forced intraday exit
- manual exit support
- unit, integration, and simulation test coverage for critical flows
- runnable CLI/bootstrap application for demonstration

## 2.2 Out of Scope

The following are intentionally deferred beyond MVP 1.0:

- live broker integration
- paper trading integrations
- distributed event processing
- multi-tenant runtime
- UI/web dashboard
- REST API layer
- database persistence and recovery
- advanced scanner scaling across large universes
- dynamic plugin discovery from external JARs
- Spring-based runtime wiring inside core modules
- machine learning or predictive behavior
- advanced Phase 4 exit behavior

---

# 3. Recommended Build Structure

## 3.1 Maven Structure

Use **one parent Maven project with multiple child modules**.

This is the recommended structure:

```text
xit/
  pom.xml
  trading-domain/
  orchestrator-core/
  market-data-engine/
  indicators-engine/
  strategy-composition/
  entry-engine/
  trade-lifecycle/
  exit-engine/
  execution-infrastructure/
  app-runner/
  docs/
```

## 3.2 Why Multi-Module Instead of Single Module

A single module would make it too easy to accidentally violate module boundaries, introduce hidden coupling, and weaken compile-time dependency enforcement.

The repository documentation explicitly defines a modular architecture with strict dependency direction and event-only communication between modules. A multi-module build supports those rules from the beginning rather than postponing them.

## 3.3 Why `app-runner` Exists

The `app-runner` module should remain thin and exist only to:

- assemble the modules
- load configuration
- bootstrap the orchestrator
- load sample data
- run the deterministic demo scenario

This allows the core engines to stay framework-neutral and testable.

---

# 4. Technology and Runtime Approach

## 4.1 Core Implementation Style

The core should start as **pure Java**.

Use:

- interfaces for contracts
- constructor-based wiring
- immutable models where practical
- explicit state transitions
- explicit event publication and subscription

## 4.2 Spring Recommendation

Do **not** introduce Spring into the core modules in MVP 1.0.

Spring may be introduced later in an outer application layer if needed for:

- configuration management
- profiles
- scheduling
- external adapters
- REST APIs
- operational wiring

For MVP 1.0, manual composition in `app-runner` is preferable because it keeps dependencies visible and preserves deterministic behavior.

---

# 5. Architectural Rules That Shape the MVP

The implementation of MVP 1.0 must obey the following repository rules:

- all cross-module interaction must be event-driven
- module responsibilities must remain separate
- the domain module must contain only data and validation
- indicators must be computed once per instrument and avoid full-history recomputation per tick
- entry logic must only generate signals
- exit logic must remain independent from entry logic
- stop loss must never decrease
- trade transitions must be explicit and valid
- execution must remain broker-agnostic
- extensibility must use registry/factory patterns instead of hardcoded branching

These rules should influence every sprint and every class design decision.

---

# 6. MVP Module Responsibilities

## 6.1 `trading-domain`

Minimum MVP responsibilities:

- define shared data models
- define immutable event payloads
- validate constructor inputs
- avoid business logic

Key candidate classes:

- `Instrument`
- `Tick`
- `Candle`
- `TradeContext`
- `TradeState`
- `TradeSide`
- `TradePhase`
- `EntrySignal`
- `ExitDecision`
- `OrderRequest`
- `OrderResponse`
- `OrderStatus`
- `IndicatorKey`
- `IndicatorValue`
- core event classes

## 6.2 `orchestrator-core`

Minimum MVP responsibilities:

- provide in-process event bus
- register subscribers
- route immutable events
- preserve deterministic dispatch order
- bootstrap module interaction

Important note:

For MVP, use **synchronous deterministic dispatch** rather than asynchronous execution. Concurrency can be introduced later once core behavior is proven stable.

## 6.3 `market-data-engine`

Minimum MVP responsibilities:

- read deterministic sample feed
- normalize tick data
- aggregate 1-minute candles
- publish market events

## 6.4 `indicators-engine`

Minimum MVP responsibilities:

- expose indicator contracts
- separate algorithm from indicator state
- support incremental updates
- compute once per instrument
- publish indicator updates

Recommended MVP indicators:

- `EMA`
- `ATR`

## 6.5 `strategy-composition`

Minimum MVP responsibilities:

- define condition interfaces
- support composable logical conditions
- evaluate deterministic strategy trees

Recommended MVP condition set:

- price above EMA
- EMA crossover
- AND / OR / NOT combinators

## 6.6 `entry-engine`

Minimum MVP responsibilities:

- accept entry context
- evaluate one strategy
- generate entry signal events
- remain free of risk and exit logic

Recommended MVP strategy:

- simple EMA breakout or EMA crossover

## 6.7 `trade-lifecycle`

Minimum MVP responsibilities:

- create trade context from signal
- validate transitions
- maintain active trade state
- consume execution and exit events
- publish completion events

## 6.8 `exit-engine`

Minimum MVP responsibilities:

- update stop loss monotonically
- apply hard safety rules
- manage phase progression
- activate capital protection before profit protection
- support time-based and manual exit
- publish exit trigger events

Recommended MVP phases:

- Phase 1: initial risk
- Phase 2: capital protection
- Phase 3: profit ownership

Phase 4 should remain deferred.

## 6.9 `execution-infrastructure`

Minimum MVP responsibilities:

- define broker adapter contract
- implement simulation adapter only
- accept order requests
- publish order events and fills

## 6.10 `app-runner`

Minimum MVP responsibilities:

- compose modules manually
- start a session
- feed market data
- run one deterministic instrument scenario
- log results

---

# 7. Sprint Plan Overview

Recommended MVP plan: **8 sprints**.

This gives enough room to build safely around the exit engine and lifecycle without compressing critical validation work.

The sprints are:

1. Sprint 0 — Build and architecture skeleton
2. Sprint 1 — Domain and event model
3. Sprint 2 — Orchestrator and event bus
4. Sprint 3 — Market data engine
5. Sprint 4 — Indicators engine
6. Sprint 5 — Strategy composition and entry engine
7. Sprint 6 — Trade lifecycle and simulation execution
8. Sprint 7 — Exit engine and MVP hardening

---

# 8. Detailed Sprint Breakdown

## Sprint 0 — Build and Architecture Skeleton

### Goal

Create the repository structure and build system so development starts on a safe architectural foundation.

### Tasks

#### Build and project setup

- create parent `pom.xml`
- add child modules
- align module names with architecture documentation
- configure Java version
- configure test plugins
- configure code coverage tooling

#### Module layout

Create package roots for each module.

Examples:

- `com.kb.trading.domain`
- `com.kb.trading.orchestrator`
- `com.kb.trading.marketdata`
- `com.kb.trading.indicators`
- `com.kb.trading.strategy`
- `com.kb.trading.entry`
- `com.kb.trading.lifecycle`
- `com.kb.trading.exit`
- `com.kb.trading.execution`

#### Dependency control

- ensure `trading-domain` has no project-module dependencies
- keep module dependencies aligned to documented direction
- keep `orchestrator-core` free of business logic
- do not let exit depend on entry

#### Engineering baseline

- add JUnit 5
- add Mockito where useful
- add root test command documentation
- prepare root README section for future build instructions if needed

### Definition of Done

- root build executes successfully
- all modules compile with empty scaffolding
- dependency structure matches architecture rules

### Risks / Notes

- avoid creating a generic “core” dump module
- avoid framework bootstrapping here

---

## Sprint 1 — Domain and Event Model

### Goal

Establish the immutable language of the system before implementing behavior.

### Tasks

#### Common value objects

Create:

- `Money`
- `Percentage`
- `Direction`
- `TimeRange`

#### Instrument and market models

Create:

- `Instrument`
- `InstrumentType`
- `Exchange`
- `Tick`
- `Candle`

#### Trade models

Create:

- `TradeContext`
- `TradeState`
- `TradeSide`
- `TradePhase`
- `Position`

#### Order models

Create:

- `OrderRequest`
- `OrderResponse`
- `OrderStatus`
- `ExecutionReport`
- `Fill`

#### Strategy and indicator models

Create:

- `EntrySignal`
- `ExitDecision`
- `IndicatorKey`
- `IndicatorValue`
- `IndicatorState`

#### Event payloads

Create immutable events such as:

- `MarketTickEvent`
- `IndicatorUpdatedEvent`
- `EntrySignalEvent`
- `TradeOpenedEvent`
- `ExitTriggeredEvent`
- `OrderFilledEvent`
- `TradeCompletedEvent`

#### Validation

- add constructor validation
- keep models free of service logic
- avoid mutable setters where practical

### Definition of Done

- domain model set is sufficient for the next sprints
- all event payloads are immutable
- domain logic remains data-only

### Test Tasks

- constructor validation tests
- enum correctness tests
- event immutability tests
- null/invalid input tests

### Risks / Notes

- resolve the exact MVP `TradeState` enum early and document whether `PARTIALLY_FILLED` is included in MVP or deferred

---

## Sprint 2 — Orchestrator and Event Bus

### Goal

Implement the event-driven backbone that all engines use to communicate.

### Tasks

#### Event bus contracts

Create:

- `EventBus`
- `EventDispatcher`
- `EventListener<T>`
- `Subscription` model if needed

#### Deterministic dispatch implementation

- support publish/subscribe
- preserve event order
- route events by type
- isolate listener failures
- avoid blocking behavior where possible

#### Trading bootstrap

Create:

- `TradingEngine`
- `SessionManager`

#### Manual composition

- wire a minimal start-up path in `app-runner`
- keep runtime wiring explicit and code-based

### Definition of Done

- events can be published and received by multiple listeners
- listener execution order is deterministic
- system startup and shutdown are possible in-process

### Test Tasks

- event routing tests
- ordering tests
- multiple subscriber tests
- error isolation tests

### Risks / Notes

- prefer synchronous dispatch in MVP to guarantee reproducibility
- do not embed any business logic in orchestrator classes

---

## Sprint 3 — Market Data Engine

### Goal

Create a deterministic data source that can drive the platform end-to-end.

### Tasks

#### Feed abstraction

Create:

- `MarketDataFeed`
- `HistoricalFeed` or `SimulationFeed`

#### Input data support

- add deterministic sample CSV or fixture-driven feed
- support one instrument in MVP
- support ordered tick delivery

#### Normalization and aggregation

- normalize raw input
- aggregate 1-minute candles
- handle partial candle close logic cleanly
- calculate consistent candle bucket boundaries

#### Event output

Publish:

- `MarketTickEvent`
- optional candle event if useful internally

### Definition of Done

- market data can be read and transformed into events deterministically
- candle aggregation is correct for test fixtures

### Test Tasks

- ordered tick processing
- candle aggregation accuracy
- bucket boundary tests
- missing or malformed data handling tests

### Risks / Notes

- avoid embedding indicator or strategy logic in market data module
- avoid multiple timeframes until MVP proves stable

---

## Sprint 4 — Indicators Engine

### Goal

Create the first reusable computed data layer shared by entry and exit behaviors.

### Tasks

#### Indicator contracts

Create:

- streaming indicator contract
- optional batch indicator contract
- algorithm/state separation

#### Registry and factories

Create:

- `IndicatorRegistry`
- `IndicatorFactory`
- `IndicatorDefinition`

#### State isolation

- isolate state by instrument
- ensure algorithms remain stateless
- create an indicator value store

#### MVP indicators

Implement:

- `EMA`
- `ATR`

#### Event publication

- publish `IndicatorUpdatedEvent`
- allow indicator consumers to subscribe without direct coupling

### Definition of Done

- indicators update incrementally from market input
- EMA and ATR are available for downstream evaluation
- indicator state is isolated and reusable

### Test Tasks

- EMA correctness tests
- ATR correctness tests
- insufficient data tests
- state isolation tests
- streaming consistency tests

### Risks / Notes

- avoid full-history recomputation per update
- keep algorithms independent from storage/state objects

---

## Sprint 5 — Strategy Composition and Entry Engine

### Goal

Generate the first valid entry signal while preserving strict boundaries.

### Tasks

#### Strategy composition

Create:

- `Condition`
- `ConditionContext`
- `ConditionResult`
- `AndCondition`
- `OrCondition`
- `NotCondition`

#### MVP condition implementations

Examples:

- price above EMA
- short EMA crosses long EMA
- price/indicator threshold checks

#### Entry engine contracts

Create:

- `EntryStrategy`
- `EntryContext`
- `EntryStrategyRegistry`
- `EntryStrategyFactory`

#### One MVP strategy

Implement one strategy only, such as:

- EMA breakout
- EMA crossover

#### Signal generation

- evaluate deterministic entry rules
- generate `EntrySignalEvent`
- prevent unnecessary duplicate signals for same evaluation window

### Definition of Done

- one strategy produces a deterministic entry signal from indicator inputs
- entry engine remains free of trade management and risk management logic

### Test Tasks

- condition truth tests
- composite condition tests
- strategy evaluation tests
- signal generation tests
- duplicate signal suppression tests

### Risks / Notes

- keep strategy selection registry-based
- avoid hardcoded `if/else` strategy branching patterns

---

## Sprint 6 — Trade Lifecycle and Simulation Execution

### Goal

Convert entry signals into executable simulated trades that progress through a valid lifecycle.

### Tasks

#### Trade state machine

Create:

- `TradeStateMachine`
- transition validation rules
- explicit allowed transitions

#### Trade coordination

Create:

- `TradeCoordinator`
- active trade repository/store
- trade update logic

#### Execution infrastructure contracts

Create:

- `BrokerAdapter`
- `BrokerRegistry`
- `OrderExecutor`
- `OrderManager`

#### Simulation mode only

Implement:

- `SimulationBrokerAdapter`
- deterministic order fill behavior
- fill event publication

#### Lifecycle integration

- consume `EntrySignalEvent`
- create `TradeContext`
- issue `OrderRequest`
- consume fill events
- move trade to `ACTIVE`

### Definition of Done

- a signal can produce a simulated filled trade
- valid lifecycle transitions succeed
- invalid transitions are rejected

### Test Tasks

- state transition validation tests
- invalid transition rejection tests
- order placement tests
- simulation fill tests
- entry-to-filled integration tests

### Risks / Notes

- trade state updates must remain atomic and explicit
- execution must remain broker-agnostic even in simulation mode

---

## Sprint 7 — Exit Engine and MVP Hardening

### Goal

Implement the most critical product behavior: deterministic active-trade exit management.

### Tasks

#### Exit context and contracts

Create:

- `ExitContext`
- `ExitStrategy`
- `ExitStrategyRegistry`
- `StopLossEngine`

#### Hard safety and monotonic stop-loss

Implement:

- initial stop-loss setup
- hard safety enforcement
- monotonic stop-loss updates only
- rejection of any stop decrease

#### Phase progression

Implement:

- Phase 1: initial risk
- Phase 2: capital protection
- Phase 3: profit ownership

#### Ownership logic

Implement one ownership approach such as:

- milestone-based ownership
or
- simple progressive trailing protection after capital protection

#### Forced exits

Implement:

- end-of-day exit
- manual exit override

#### Full integration

- consume price/indicator/trade updates as needed
- publish `ExitTriggeredEvent`
- let lifecycle and execution complete the trade

#### MVP hardening

- add simulation scenarios
- add integration tests for complete trade lifecycle
- add logs for transitions and exit decisions
- document default config values

### Definition of Done

- stop loss only tightens
- exit behavior is deterministic
- time-based intraday closure always works
- manual exit is always allowed
- trades can reach `COMPLETED` through exit flow

### Test Tasks

- stop-loss monotonicity tests
- phase transition tests
- hard safety tests
- ownership behavior tests
- end-of-day exit tests
- manual exit tests
- full trade completion tests
- profitable / losing / sideways / volatile simulation tests

### Risks / Notes

- this sprint should receive the highest review and test focus
- do not introduce adaptive or predictive behavior
- do not make exit depend on entry strategy details

---

# 9. Suggested Milestones

## Milestone A — Architecture Ready

Reached after Sprint 2 when:

- modules compile
- event bus works
- domain/event model exists

## Milestone B — Signal Generation Ready

Reached after Sprint 5 when:

- sample market data flows through indicators
- one strategy generates entry signals

## Milestone C — Trade Execution Ready

Reached after Sprint 6 when:

- lifecycle and simulation execution are integrated

## Milestone D — Product-Core Ready

Reached after Sprint 7 when:

- exit engine controls live trade state deterministically
- end-to-end MVP demo passes

---

# 10. Testing Strategy by Sprint

## Unit Testing Focus

Apply in every sprint, especially for:

- domain validation
- indicators
- conditions
- lifecycle transitions
- exit logic

## Integration Testing Focus

Begin no later than Sprint 5 and expand through Sprint 7:

- indicator -> strategy -> signal
- entry -> lifecycle -> execution
- exit -> lifecycle -> completion

## Simulation Testing Focus

By final MVP hardening, cover:

- profitable trade scenario
- losing trade scenario
- sideways trade scenario
- high-volatility scenario

## Coverage Priority

Highest coverage goals should apply to:

- exit-engine
- trade-lifecycle
- indicators-engine

---

# 11. Deferred Backlog After MVP 1.0

After MVP 1.0 is stable, the next layer of work should include:

- full scanner-engine implementation
- multi-instrument evaluation
- broader strategy library
- more indicators
- paper trading mode
- live broker adapters
- persistence/recovery
- REST API layer
- UI layer
- Spring-based outer application shell
- plugin packaging and external discovery

This ordering preserves architectural cleanliness and allows the product to scale outward from a stable core.

---

# 12. Final Recommendation

MVP 1.0 should be built as a **deterministic, simulation-first, pure-Java, multi-module Maven system**.

The success criteria are not “feature count.”

The success criteria are:

- architectural correctness
- valid module separation
- executable end-to-end flow
- lifecycle correctness
- exit-engine safety
- deterministic reproducibility
- strong automated test coverage

If these are achieved, the platform will have a reliable foundation for scanner expansion, broker integrations, APIs, and Spring-based runtime shells later.
