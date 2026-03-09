![Comparator](https://www.dropbox.com/scl/fi/qk3sqt46garmoyoqei3pa/comparator-banner.png?rlkey=mt61gg70qxlvond5sys82jac6&st=z7pytba9&raw=1)

> ### The AI agent built for Minecraft.

[![Github](https://img.shields.io/badge/github-comparator-FFBD59?logo=github)](https://github.com/fletchly/comparator)
[![Modrinth](https://img.shields.io/badge/modrinth-comparator-00AF5C?logo=modrinth)](https://modrinth.com/project/MQoLAFN8)
![Release](https://img.shields.io/github/v/release/fletchly/comparator)
[![Documentation](https://img.shields.io/badge/read%20the%20docs-gray?logo=gitbook&logoColor=FFBD59)](https://fletchly.gitbook.io/comparator-docs/)
![Minecraft Version](https://img.shields.io/badge/minecraft-1.21.x-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)
[![Build](https://github.com/fletchly/comparator/actions/workflows/build.yml/badge.svg)](https://github.com/fletchly/comparator/actions/workflows/build.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/fletchly/comparator/badge/main)](https://www.codefactor.io/repository/github/fletchly/comparator/overview/main)

Comparator brings a fully conversational AI agent to your Paper server; with multi-turn memory, built-in tools, and an API to extend it on your own.

---

## Features

### 🤖 Conversational AI Agent
Embed a fully conversational AI agent directly into your Minecraft server. Comparator maintains context across messages for natural, coherent dialogue; not just one-shot responses.

### 🔧 Built-In Tools
The agent can to more than just chat. Comparator ships with several built-in tools the AI can invoke to give accurate, grounded answers:

| Tool | Description |
|------|-------------|
| **Web Search** | Searches the web for up-to-date information, so the agent isn't limited to its training data |
| **Current Date** | Retrieves the current date, helping the agent reason about how recent its knowledge is |
| **Game Version** | Fetches the server's running Minecraft version so answers are version-accurate |
| **Player Info** | Retrieves the player's surroundings, inventory, nearby entities, biome, game mode, and what they're looking at. This enables contextual in-game assistance |

### 🧩 Extensible Tool API
Third-party plugins can register their own tools using the Comparator API. Defining a tool is as simple as annotating a function:

```kotlin
@ToolFunction("give_item", "Gives an item to the requesting player")
suspend fun giveItem(
    @ToolParameter("The item to give") item: String,
    context: ToolContext
): GiveResult {
    val player = context.bukkitPlayer ?: throw ToolException("No player context", null)
    // ...
}

// Register during ToolRegistrationEvent
event.registry.register(tool(::giveItem))
```

The API handles parameter validation, type checking, serialization, and error reporting automatically. See the [API docs](https://fletchly.github.io/comparator/) for full details.

### 🔒 Designed for Responsible AI Use
Comparator was built with safety and transparency in mind. This sets it apart from plugins that simply wrap an AI API with no guardrails:

- **Permission-gated access** — All commands and public chat interactions respect Bukkit permissions, giving server admins fine-grained control over who can use the agent and who can manage it.
- **Configurable system prompt** — The agent's behavior, persona, and constraints are fully configurable. The default prompt explicitly instructs the agent to stay on-topic, avoid fabricating information, and flag when its knowledge may be outdated.
- **Context scoping** — Conversations are isolated per-player, per-console, and for public chat. A player cannot read or pollute another player's conversation history.
- **Automatic context expiry** — Conversation history expires after a configurable period of inactivity, limiting unintended data retention.
- **Tool result and parameter validation** — Tool inputs are validated against declared parameter types and constraints before execution. The agent cannot invoke a tool with malformed arguments.
- **Queue-based message handling** — Each conversation runs through a bounded channel. If a player floods the agent with messages, excess requests are rejected gracefully rather than queued unboundedly.
- **Structured error handling** — Tool failures are surfaced to the model as descriptive errors, not raw stack traces or internal state.

### ⚡ High-Performance, Non-Blocking Architecture
Built on Kotlin coroutines for fully non-blocking execution. AI requests, tool calls, and context operations never block the server thread, so the agent has no impact on server tick performance.

### 🌐 Flexible AI Provider Support
Integrates with [Ollama](https://ollama.com), supporting both cloud-hosted and self-hosted models. Configure your model, base URL, and optional API key in `comparator.conf`.

---

## Installation

1. Download the plugin `.jar` from [Modrinth](https://modrinth.com/project/MQoLAFN8) or [GitHub Releases](https://github.com/fletchly/comparator/releases/latest)
2. Place the `.jar` in your server's `plugins/` folder
3. Restart your server
4. Follow the [quickstart guide](https://fletchly.gitbook.io/comparator-docs/getting-started/quickstart) to complete setup

---

## Usage

**Ask in private (DM-style):**
```
/ask <prompt>
```

**Mention in public chat:**
```
@bot <prompt>
```

For full usage information, see the [documentation](https://fletchly.gitbook.io/comparator-docs/).

---

## Support

- [Plugin Documentation](https://fletchly.gitbook.io/comparator-docs/)
- [API Documentation](https://fletchly.github.io/comparator/)
- [Discord](https://discord.gg/PVPfrsZTSr)
- [GitHub Issues](https://github.com/fletchly/comparator/issues)

---

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push and open a Pull Request

Please follow the existing code style and include tests where applicable. For more details, see [CONTRIBUTING.md](https://github.com/fletchly/comparator/blob/main/CONTRIBUTING.md).

---

## Credits

- Main icon: *circuit* by Skena Grafis from [The Noun Project](https://thenounproject.com/icon/circuit-8187377/) (CC BY 3.0)
