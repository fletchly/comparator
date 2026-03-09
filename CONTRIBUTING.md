# Contributing to Comparator

Thank you for your interest in contributing! Contributions of all kinds are welcome — bug reports, feature suggestions, documentation improvements, and code changes all help make Comparator better.

---

## Reporting Bugs

Before opening a bug report, check the [existing issues](https://github.com/fletchly/comparator/issues) to see if it has already been reported. If not, [open a new one](https://github.com/fletchly/comparator/issues/new?template=bug_report.md) and include:

- A clear title and description of the problem
- Steps to reproduce, and expected vs. actual behavior
- Your Paper version, Comparator version, and the AI model/provider you're using
- Any relevant server logs or stack traces

---

## Suggesting Features

Feature requests are welcome. Open an [issue](https://github.com/fletchly/comparator/issues/new?template=feature_request.md) with a description of the proposed feature, why it would be useful, and any examples or mockups if applicable.

If you want to discuss an idea before committing to a full proposal, feel free to bring it up on [Discord](https://discord.gg/PVPfrsZTSr) first.

---

## Architecture Overview

Comparator is structured as a multi-module Gradle project following a **hexagonal (ports and adapters) architecture**. Understanding this layout will help you find your way around the codebase quickly.

### Modules

| Module | Purpose |
|--------|---------|
| `comparator-api` | The public-facing Tool API. This is what third-party plugins depend on to register custom tools. Contains the `@ToolFunction`, `@ToolParameter`, and `@AllowedValues` annotations, the `Tool`, `ToolContext`, and `ToolResult` models, the `ToolBuilder` (`tool()` function), and `ToolRegistrationEvent`. |
| `comparator-core` | The application core. Contains all business logic (conversation management, tool execution, context lifecycle) expressed entirely in terms of abstract ports. Has no dependency on Paper or any external framework. |
| `comparator-common` | Framework-agnostic adapters and shared infrastructure. Contains the Ollama AI provider, Caffeine-backed context store, HTTP client configuration, web search and current date tools, and HOCON config loading. |
| `comparator-paper` | The Paper-specific platform layer. Contains the plugin entry point, Bukkit event listeners, commands, chat integration, Paper-specific tools (game version, player info), DI bootstrapping via Koin, and all configuration models. |

### Ports and Adapters

The boundary between `comparator-core` and the rest of the project is enforced through **ports** — interfaces that the core defines and the outer layers implement.

- `port/in` — Inbound ports, called by adapters to drive the application (e.g., `MessageSender`, `ContextLifecycle`, `ToolExecutor`)
- `port/out` — Outbound ports, implemented by adapters that the application depends on (e.g., `AIPort`, `ContextPort`, `ChatPort`, `LogPort`)

If you're adding behavior to the core, express external dependencies as new `port/out` interfaces rather than importing concrete implementations directly.

### Dependency Injection

All wiring is handled by [Koin](https://insert-koin.io/). Module definitions live in the `di/` package of each subproject. If you add a new class that needs to be injected, register it in the appropriate module.

---

## Pull Requests

### Getting Started

1. Fork the repository and clone your fork
2. Create a branch for your changes: `git checkout -b feat/my-feature` or `git checkout -b fix/bug-description`
3. Make your changes, then open a [pull request](https://github.com/fletchly/comparator/compare) targeting `main`

### Guidelines

- Follow standard Kotlin conventions and match the existing code style
- Write clear, concise commit messages
- Ensure the project compiles and all existing tests pass before opening a PR
- Add or update tests for any new functionality or bug fixes — the project uses `kotlin.test` with MockK for mocking
- Update documentation (README, plugin description, API docs) as needed
- Target the latest supported Minecraft version unless the change is version-specific

### Adding a Built-In Tool

Built-in tools that ship with Comparator live in `comparator-common` (if platform-agnostic, e.g., web search, current date) or `comparator-paper` (if they require the Bukkit API, e.g., game version, player info).

To add one:
1. Define a handler function annotated with `@ToolFunction` and `@ToolParameter`
2. Create a `Tool` instance using the `tool()` builder
3. Register it conditionally in `ToolRegistrationEvents` based on a config flag in `BuiltInToolOptions` and `ToolConfig`

If you're building a tool for your own plugin rather than contributing one to Comparator itself, see the [API documentation](https://fletchly.github.io/comparator/) — your plugin should listen for `ToolRegistrationEvent` independently rather than submitting a PR here.

---

## Running Locally

```bash
# Build the plugin
./gradlew build

# Run a local Paper test server (downloads Paper automatically)
./gradlew runServer
```

Tests can be run with:

```bash
./gradlew test
```
