package org.example.taxcalculator;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxController {

  private final ChatClient chatClient;

  public TaxController(ChatClient.Builder builder,
                       McpSyncClient mcpSyncClient) {
    this.chatClient = builder
      .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
      .build();
  }

  @GetMapping("/tax")
  public String calculateTax(String message) {

    String SYSTEM_PROMPT = """
         You are a tax calculation assistant.\s
         Your task is to parse from the user's message:
         - income amount in USD
         - date of income in format yyyy-MM-dd
         - tax rate (percentage)
      \s
         Then fetch the currency exchange rate for USD to Georgian Lari (GEL) for the given date using the provided MCP tool.
      \s
         Calculate the tax value in GEL for given income amount, fetched currency exchange rate, and tax rate (percentage) using the provided MCP tool.
      \s

         Finally, respond in this format:
             "The tax value for the [month name] is [tax] Georgian Lari.\s
             Income = [income USD, income Georgian Lari],\s
             Currency exchange rate = [currency exchange rate],\s
             Tax Rate = [tax rate],
             Date of income = [date yyyy-MM-dd]."
      \s""";

    return chatClient.prompt()
      .system(SYSTEM_PROMPT)
      .user(message)
      .call()
      .content();
  }
}
