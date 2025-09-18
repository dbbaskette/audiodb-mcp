# 🎧 AudioDB MCP Server

<p align="center">
  <img src="https://img.shields.io/badge/Java-21+-ed8b00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21+ badge" />
  <img src="https://img.shields.io/badge/Spring%20Boot-WebFlux-6db33f?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot WebFlux badge" />
  <img src="https://img.shields.io/badge/MCP-ready-007acc?style=for-the-badge&logo=github&logoColor=white" alt="MCP Ready badge" />
</p>

<sub><sup>A vivid, MCP-compliant gateway into TheAudioDB catalog.</sup></sub>

---

<details>
<summary><strong>Table of Contents</strong></summary>

- [🎯 Why AudioDB MCP?](#-why-audiodb-mcp)
- [🚀 Quick Start](#-quick-start)
- [🧰 MCP Tools Cheat Sheet](#-mcp-tools-cheat-sheet)
- [🌐 HTTP Test Endpoints](#-http-test-endpoints)
- [⚙️ Configuration Matrix](#️-configuration-matrix)
- [🧪 Build & Validate](#-build--validate)
- [🗂️ Project Layout](#️-project-layout)
- [🧭 Integration Tips](#-integration-tips)
- [📚 Additional Notes](#-additional-notes)

</details>

## 🎯 Why AudioDB MCP?

| Icon | Capability | What You Get |
| --- | --- | --- |
| 🎤 | **Artist Spotlight** | Enriched biographies, genre/style metadata, label info, and imagery pulled straight from TheAudioDB. |
| 💿 | **Discography Digest** | Ordered album lists with release years, genres, labels, and artwork links for fast catalog exploration. |
| ⚡ | **Reactive Pipeline** | WebFlux-based client with retry/backoff and non-blocking HTTP calls to keep responses snappy. |
| 🤝 | **MCP Native** | Drop-in Model Context Protocol tools that can be wired into any MCP-aware client or orchestration layer. |

> 💡 **Color legend:** `![Primary](https://img.shields.io/badge/-6DB33F-6DB33F?style=flat-square)` reactive services, `![Accent](https://img.shields.io/badge/-007ACC-007ACC?style=flat-square)` MCP connectivity, `![Highlight](https://img.shields.io/badge/-ED8B00-ED8B00?style=flat-square)` JVM runtime.

## 🚀 Quick Start

1. **Prerequisites** — Install `Java 21+` and `Maven 3.6+`.
2. **Bootstrap** — First run only: `chmod +x run-mcp.sh`.
3. **Launch** — `./run-mcp.sh` (defaults to port `8090`).
4. **Verify** — `curl http://localhost:8090/actuator/health` for a quick health check.

> ✅ The helper script compiles, packages, and launches the MCP server in one pass.

## 🧰 MCP Tools Cheat Sheet

| Tool | Parameters | Returns | Ideal For |
| --- | --- | --- | --- |
| 🎙️ `search_artist` | `artistName` *(required)* | Biography, genre/style, formation year, label, mood, image URLs, MusicBrainz ID. | Prompt enrichment, knowledge grounding, content generation. |
| 📀 `get_artist_discography` | `artistName` *(required)* | Album count, release years, genres, labels, descriptions, cover art URLs. | Playlist building, catalog QA, recommendation pipelines. |

Both tools log activity via SLF4J so you can trace invocations in `server.log` during development sessions.

## 🌐 HTTP Test Endpoints

| Method | Endpoint | Sample | Purpose |
| --- | --- | --- | --- |
| `GET` | `/test/artist?name={artist}` | `/test/artist?name=coldplay` | Calls `search_artist` directly and returns a formatted plain-text profile. |
| `GET` | `/test/discography?artist={artist}` | `/test/discography?artist=radiohead` | Calls `get_artist_discography` and prints an ordered album list. |
| `GET` | `/test/help` | `/test/help` | Quick reminder of available test routes. |
| `GET` | `/actuator/health` | `/actuator/health` | Spring Boot actuator health probe for ops integration. |

> 📎 The test controller relies on the lightweight `SimpleAudioDbClient`, so your MCP tools stay untouched while you iterate.

## ⚙️ Configuration Matrix

| Scope | Property | Default | Notes |
| --- | --- | --- | --- |
| Server | `server.port` | `8090` | Change if 8090 conflicts with existing services. |
| AudioDB API | `audiodb.api.base-url` | `https://www.theaudiodb.com/api/v1/json` | Switch to mirrors or mock servers as needed. |
| AudioDB API | `audiodb.api.api-key` | `2` | The public demo key; replace with your private key for production. |
| MCP | `spring.ai.mcp.server.streamable-http.mcp-endpoint` | `/mcp/audio` | Align with your MCP client configuration. |

Configuration lives in `src/main/resources/application.yml`; override via environment variables or JVM system properties in deployment environments.

## 🧪 Build & Validate

| Task | Command |
| --- | --- |
| Clean & compile | `mvn clean compile` |
| Run locally | `mvn spring-boot:run` |
| Package JAR | `mvn clean package` then `java -jar target/audiodb-mcp-server-1.0.0.jar` |

`run-mcp.sh` wraps the package + run flow for convenience; feel free to tweak it for custom JVM flags or profiles.

## 🗂️ Project Layout

```text
src/main/java/com/audiodb/mcp/
├── AudioDbMcpServerApplication.java      # Spring Boot entry point
├── controller/
│   └── TestController.java               # Plain HTTP testing harness
├── model/
│   ├── Album.java                        # Album DTO
│   ├── Artist.java                       # Artist DTO
│   ├── ArtistSearchResponse.java         # Search API wrapper
│   └── DiscographyResponse.java          # Discography API wrapper
└── service/
    ├── AudioDbToolService.java           # MCP tool implementations
    ├── TheAudioDbClient.java             # Reactive WebClient gateway
    └── SimpleAudioDbClient.java          # Blocking client for tests
```

## 🧭 Integration Tips

- 🧱 **MCP Clients** — Map the tool names (`search_artist`, `get_artist_discography`) directly into your MCP client registry.
- 🕒 **Rate Limits** — The public key `2` allows ~2 requests/sec; add in-memory caching if you batch prompts.
- 📝 **Logging** — Tail `server.log` or use `mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.root=DEBUG` for deeper traces.
- 🔁 **Resilience** — `TheAudioDbClient` already retries with exponential backoff; complement with circuit breakers if you deploy in production.

## 📚 Additional Notes

- The project targets educational and internal tooling scenarios—always review TheAudioDB licensing before shipping to end users.
- Consider trimming or summarizing biographies before displaying them in UIs; some responses exceed 500 characters.
- Contributions are welcome: linting, broader test coverage, or alternative MCP clients would make great next steps.

> 🎶 "Knowledge is power, but rhythm makes it memorable." Happy building!
