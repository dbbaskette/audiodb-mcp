package com.audiodb.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AudioDbMcpServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(AudioDbMcpServerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting AudioDB MCP Server Application...");
        SpringApplication.run(AudioDbMcpServerApplication.class, args);
        logger.info("AudioDB MCP Server Application started successfully");
    }
}