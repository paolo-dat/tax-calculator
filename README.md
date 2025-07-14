
# Tax Calculation Assistant

A Spring Boot application that uses **Spring AI** for natural-language parsing of user tax inquiries and delegates currency conversion and tax calculation to the **tax-mcp-server** tools (MCP server).

This service turns free-text questions into structured calculations using AI and external APIs.

---

## 📜 Overview

This service exposes a single HTTP endpoint:

```
GET /tax
```

It:
- Parses user messages with **Spring AI** (`ChatClient`)
- Uses MCP server tools to:
  - Fetch the USD-to-GEL exchange rate for a given date
  - Calculate the tax amount in GEL
- Returns a human-readable answer with full details

---

## ⚙️ How It Works

### 🧩 1. User Query

The user sends a free-text question, for example:

```
"I earned 2000 dollars on 2024-01-15. My tax rate is 20%."
```

---

### 🧩 2. AI Parsing with Spring AI

The controller uses Spring AI’s `ChatClient` to parse the message:

- Extracts:
  - Income amount in USD
  - Date (yyyy-MM-dd)
  - Tax rate (%)

- System prompt instructs the AI:

```
You are a tax calculation assistant.
Your task is to parse from the user's message:
- income amount in USD
- date of income in format yyyy-MM-dd
- tax rate (percentage)

Then fetch the currency exchange rate for USD to Georgian Lari (GEL) for the given date using the provided MCP tool.

Calculate the tax value in GEL for given income amount, fetched currency exchange rate, and tax rate (percentage) using the provided MCP tool.

Finally, respond in this format:
"The tax value for the [month name] is [tax] Georgian Lari.
Income = [income USD, income Georgian Lari],
Currency exchange rate = [currency exchange rate],
Tax Rate = [tax rate],
Date of income = [date yyyy-MM-dd]."
```

---

### 🧩 3. MCP Server Integration

The controller is wired with:

```java
.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
```

This connects to your **tax-mcp-server**, which provides two tools:

✅ **Fetch USD Exchange Rate**  
- Fetches the USD to GEL rate for a given date.  
- Delegated to MCP server’s `fetchUsdRate()`.

✅ **Calculate Tax Value**  
- Calculates the tax amount in GEL.  
- Delegated to MCP server’s `calculateTaxValue()`.

---

### 🧩 4. Response

The final response is automatically formatted:

```
The tax value for January is 535.78 Georgian Lari.
Income = [2000 USD, 10715.60 GEL],
Currency exchange rate = 2.6789,
Tax Rate = 20,
Date of income = 2024-01-15.
```

---

## 📡 REST Endpoint

### `/tax`

**Method:** GET

**Query Parameter:**
- `message` – User’s natural-language tax question

**Example Call:**
```
GET /tax?message=I earned 1500 dollars on 2024-02-10 at 15 percent tax.
```

**Example Response:**
```
The tax value for February is 402.45 Georgian Lari.
Income = [1500 USD, 8049.00 GEL],
Currency exchange rate = 2.6830,
Tax Rate = 15,
Date of income = 2024-02-10.
```

---

## 🏗️ Architecture

- **Spring Boot** – REST Controller
- **Spring AI** – Natural-language parsing via `ChatClient`
- **tax-mcp-server** – External MCP server exposing currency/tax tools

