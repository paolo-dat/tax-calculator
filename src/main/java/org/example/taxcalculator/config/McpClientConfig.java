package org.example.taxcalculator.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

  @Bean
  McpSyncClient mcpSyncClient() {
    var transport = HttpClientSseClientTransport.builder("http://localhost:8081").build();
    var mcp = McpClient.sync(transport).build();
    mcp.initialize();
    return mcp;
  }
}
