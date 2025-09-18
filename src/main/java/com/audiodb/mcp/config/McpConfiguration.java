package com.audiodb.mcp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import jakarta.annotation.PostConstruct;

/**
 * Configuration class for MCP server
 */
@Configuration
@ComponentScan(basePackages = {"com.audiodb.mcp.service", "org.springaicommunity.mcp"})
@ConditionalOnProperty(
    prefix = "spring.ai.mcp.server",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class McpConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(McpConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("MCP Configuration initialized");
        logger.info("MCP Server enabled - scanning for @McpTool annotations");
        logger.info("Component scan packages: com.audiodb.mcp.service");
    }
}