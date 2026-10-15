package com.gemini.AiInte.configs;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class AiConfig {
    
    private Logger logger = LoggerFactory.getLogger(tokenCountAdvisor.class);

    @Bean
    public ChatMemory chatMemory(@NonNull JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(3)
                .build();

    }
        
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        this.logger.info("Info the chatmemory advisor using : " + chatMemory.getClass().getName());

        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return builder
                // .defaultAdvisors(new SimpleLoggerAdvisor(), new tokenCountAdvisor())
                .defaultAdvisors(memoryAdvisor ,new tokenCountAdvisor())
                .defaultSystem("You a helping coding Assistant so give ans in that way with example in java .")
                .build();
    }
}
