# 🤖 Spring AI Chatbot — Context-Aware LLM Backend

> A lightweight, production-minded AI chatbot backend built with **Java**, **Spring Boot**, and **Spring AI**, powered by the **OpenAI API**. Ask anything through a single REST endpoint — directly from your browser — and get answers from a large language model that **remembers the conversation** for as long as the application is running.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-OpenAI-6DB33F?logo=spring&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-API-412991?logo=openai&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white)

This is a **backend-only** project: there is no frontend to build or deploy. Hit the endpoint from a browser, `curl`, or Postman and the Spring AI `ChatClient` handles prompt construction, conversation memory, and the call to the LLM.

---

## 📌 Table of Contents

1. [Features](#-features)
2. [Tech Stack](#-tech-stack)
3. [Project Architecture](#-project-architecture)
4. [Directory Structure](#-directory-structure)
5. [API Endpoint](#-api-endpoint)
6. [Usage Examples](#-usage-examples)
7. [Setup & Installation](#-setup--installation)
8. [Design Decisions](#-design-decisions)

---

## ✨ Features

- **Ask anything over HTTP** — one `GET` endpoint, usable straight from the browser address bar
- **LLM-powered responses** — integrates OpenAI models through Spring AI's portable `ChatClient` API
- **Conversation memory** — the chatbot retains earlier messages, so follow-up questions ("What did I just ask?") work as expected
- **Provider-portable design** — Spring AI abstracts the model provider, so switching LLM vendors is a dependency and configuration change, not a rewrite
- **Externalized secrets** — the OpenAI API key is supplied through configuration or an environment variable, never hard-coded
- **Zero frontend dependency** — a pure backend service that any client can consume
- **Standard Spring Boot packaging** — build once, run anywhere a JVM is available

---

## 🛠 Tech Stack

### Backend
- **Java 17+**
- **Spring Boot** (Spring Web)
- **Spring AI** (`ChatClient`, chat memory advisor)
- **Maven** (build & dependency management)

### AI / LLM
- **OpenAI API** via the `spring-ai-starter-model-openai` starter

### Database
- **MySQL 8.x**
- **MySQL Connector/J** (JDBC driver)

---

## 🏗 Project Architecture

A request flows from the client through the controller into Spring AI's `ChatClient`. A memory advisor injects prior conversation history into every prompt before it is sent to the OpenAI API.

```text
┌──────────────────────────────┐
│  Client (Browser / Postman)  │   GET /ai/ask?q=<question>
└───────────────┬──────────────┘
                ▼
┌──────────────────────────────┐
│  Controller Layer            │   Maps the request, reads the `q` parameter,
│  (@RestController)           │   returns the model's answer
└───────────────┬──────────────┘
                │  prompt(question)
                ▼
┌──────────────────────────────┐
│  Spring AI ChatClient        │   Builds the prompt, applies advisors
│                              │
│   ┌──────────────────────┐   │
│   │ Chat Memory Advisor  │◄──┼── Stores & replays conversation history
│   └──────────────────────┘   │   (in-memory, lives until app restart)
└───────────────┬──────────────┘
                │  HTTPS + API key
                ▼
┌──────────────────────────────┐
│  OpenAI API (LLM)            │   Generates the completion
└───────────────┬──────────────┘
                │  response text
                ▼
        Back to the client
```

---

## 📁 Directory Structure

```text
spring-ai-chatbot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── aichatbot/
│   │   │           ├── AiChatbotApplication.java    # Spring Boot entry point
│   │   │           ├── config/
│   │   │           │   └── ChatClientConfig.java    # ChatClient + chat memory setup
│   │   │           └── controller/
│   │   │               └── AiController.java        # GET /ai/ask
│   │   └── resources/
│   │       └── application.properties               # Server, MySQL & OpenAI config
│   └── test/
│       └── java/com/aichatbot/                      # Unit / integration tests
├── pom.xml
└── README.md
```

---

## 🔗 API Endpoint

**Base URL:** `http://localhost:8080`

| HTTP Method | Endpoint | Query Parameter | Description |
|:-----------:|----------|-----------------|-------------|
| `GET` | `/ai/ask` | `q` — your question (URL-encoded) | Sends the question to the OpenAI model through Spring AI, with conversation memory applied, and returns the generated answer. |

---

## 💬 Usage Examples

### From the browser

Paste a URL straight into the address bar:

```text
http://localhost:8080/ai/ask?q=What is Spring AI?
```

Browsers encode spaces automatically. When using other tools, encode them yourself (`%20`).

### From the terminal

```bash
curl "http://localhost:8080/ai/ask?q=Explain%20dependency%20injection%20in%20one%20paragraph"
```

### Chat memory in action

Because conversation history is retained, the model can answer questions that depend on earlier messages:

```text
1) http://localhost:8080/ai/ask?q=My name is Alex and I am learning Spring Boot
2) http://localhost:8080/ai/ask?q=What is my name and what am I learning?
```

The second answer references details from the first request. The context is cleared when the application is restarted.

---

## 🚀 Setup & Installation

### Prerequisites

| Requirement | Details |
|-------------|---------|
| JDK | 17 or higher |
| Maven | 3.8+ |
| MySQL | 8.x, running locally |
| OpenAI API key | Create one at [platform.openai.com](https://platform.openai.com/api-keys) |
| Git | any recent version |

```bash
java -version
mvn -version
mysql --version
```

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

### 2. Create the MySQL database

```sql
CREATE DATABASE ai_chatbot_db;
```

### 3. Add your OpenAI API key

Open `src/main/resources/application.properties` and replace the placeholder with your own key:

```properties
# ---------- Server ----------
server.port=8080

# ---------- MySQL Datasource ----------
spring.datasource.url=jdbc:mysql://localhost:3306/ai_chatbot_db
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ---------- Spring AI / OpenAI ----------
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY

# Optional: choose a model. The property name depends on your Spring AI version:
#   Spring AI 1.x ->  spring.ai.openai.chat.options.model=<model-id>
#   Spring AI 2.x ->  spring.ai.openai.chat.model=<model-id>
```

**Security tip:** never commit a real API key. Read it from an environment variable instead:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

```bash
# Linux / macOS
export OPENAI_API_KEY=sk-...

# Windows (PowerShell)
$env:OPENAI_API_KEY="sk-..."
```

If a key is ever pushed to GitHub by mistake, revoke it immediately in the OpenAI dashboard.

### 4. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/*.jar
```

### 5. Ask your first question

Open **http://localhost:8080/ai/ask?q=Hello** in your browser.

### Troubleshooting

| Issue | Fix |
|-------|-----|
| `401 Unauthorized` from OpenAI | The API key is missing, invalid, or revoked. Check `spring.ai.openai.api-key`. |
| `429 Too Many Requests` / quota error | Your OpenAI account has hit its rate limit or has no remaining credit. |
| Context lost between questions | Chat memory is in-memory and resets on every restart. |
| `Communications link failure` | Confirm MySQL is running and the datasource URL is correct. |
| `Port 8080 already in use` | Change `server.port` or stop the conflicting process. |

> **Note:** each question is a billable OpenAI API call. Conversation memory also increases the number of tokens sent per request as the chat grows.

---

## 🧠 Design Decisions

- **Spring AI `ChatClient`:** a fluent, provider-agnostic API keeps the controller thin and avoids coupling application code to one vendor's SDK.
- **Advisor-based memory:** conversation history is applied through a chat memory advisor rather than hand-built prompt strings, keeping memory logic out of the controller.
- **In-memory conversation context:** history is held for the lifetime of the running application, which keeps the service simple and fast; it is intentionally cleared on restart.
- **Configuration over code:** the API key and model selection live in configuration, so the same build runs in any environment.
- **Backend-only scope:** a single focused endpoint keeps the project easy to read, test, and extend (streaming, per-user memory, persistence, RAG).

---

## 🔭 Future Improvements

- Per-user conversation IDs so each client gets an isolated chat history
- Persistent chat memory backed by MySQL
- Streaming responses for a real-time typing effect
- Retrieval-Augmented Generation (RAG) over custom documents

---

## 👤 Author

**Ramnivas**
Final-year Engineering Student · Java & Spring Boot Developer

🔗 GitHub:(https://github.com/rampra4)

---

⭐ If you found this project useful, consider giving it a star!
