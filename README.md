# Comparator

> The AI agent built for Minecraft.

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.x-brightgreen)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## Features
- [x] **AI-Powered Agent:** Embed a fully conversational AI agent directly into your Minecraft server.
- [x] **Ollama Integration:** Choose from a selection of cloud-based or self-hosted models thanks to seamless integration with Ollama.
- [x] **Multi-Turn Conversations:** Maintains context across messages for natural, coherent dialogue with players.
- [x] **Tool Calling:** The agent can invoke tools and take actions in response to player requests.
- [x] **Fully Concurrent:** Built on Kotlin coroutines for non-blocking, high-performance execution that won't lag your server.
- [x] **Configurable Prompting:** Customize the agent's personality, behavior, and context via config files.
- [ ] *(Coming Soon)* **Extensible Tool System:** Register custom tools to expand what the agent can do in-game.

## Requirements


This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app` and `utils` subprojects.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).