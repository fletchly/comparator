![Comparator](https://www.dropbox.com/scl/fi/qk3sqt46garmoyoqei3pa/comparator-banner.png?rlkey=mt61gg70qxlvond5sys82jac6&st=z7pytba9&raw=1)

> ### The AI agent built for Minecraft.

[![Github](https://img.shields.io/badge/github-comparator-FFBD59?logo=github)](https://github.com/fletchly/comparator)
[![Modrinth](https://img.shields.io/badge/modrinth-comparator-00AF5C?logo=modrinth)](https://modrinth.com/project/MQoLAFN8)
[![Documentation](https://img.shields.io/badge/read%20the%20docs-gray?logo=gitbook&logoColor=FFBD59)](https://fletchly.gitbook.io/comparator-docs/)
![Minecraft Version](https://img.shields.io/badge/minecraft-1.21.x-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)
![Release](https://img.shields.io/github/v/release/fletchly/comparator)
[![Build](https://github.com/fletchly/comparator/actions/workflows/build.yml/badge.svg)](https://github.com/fletchly/comparator/actions/workflows/build.yml)

## Features
- **AI-Powered Agent:** Embed a fully conversational AI agent directly into your Minecraft server.
- **Ollama Integration:** Choose from a selection of cloud-based or self-hosted models thanks to seamless integration with Ollama.
- **Multi-Turn Conversations:** Maintains context across messages for natural, coherent dialogue with players.
- **Tool Calling:** The agent can invoke tools and take actions in response to player requests.
- **Fully Concurrent:** Built on Kotlin coroutines for non-blocking, high-performance execution that won't lag your server.
- **Configurable Prompting:** Customize the agent's personality, behavior, and context via config files.
- *(Coming Soon)* **Extensible Tool System:** Register custom tools to expand what the agent can do in-game.

## Installation
1. Download the plugin `jar` from [Modrinth](https://modrinth.com/project/MQoLAFN8) or [GitHub](https://github.com/fletchly/comparator/releases/latest)
2. Place the `.jar` into your server's `plugins/` folder.
3. Restart your server
4. Follow the [quickstart guide](https://fletchly.gitbook.io/comparator-docs/getting-started/quickstart) to complete setup

## Usage
**Ask a question:**
```text
/ask <prompt>
```
**Mention in public chat:**
```text
@bot <prompt>
```
For full usage, [see the docs](https://fletchly.gitbook.io/comparator-docs/).

## Support
- [Plugin Documentation](https://fletchly.gitbook.io/comparator-docs/)
- [API Documentation](https://fletchly.github.io/comparator/)
- [Github Issues](https://github.com/fletchly/comparator/issues)

## Contributing
Contributions are welcome!

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push and open a Pull Request.

Please follow the existing code style and include tests where applicable.

For more info, see [contributing](https://github.com/fletchly/comparator/blob/main/CONTRIBUTING.md)

## Credits
- Main icon: 'circuit' by Skena Grafis from [The Noun Project](https://thenounproject.com/icon/circuit-8187377/) (CC BY 3.0)
