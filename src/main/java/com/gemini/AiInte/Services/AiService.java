package com.gemini.AiInte.Services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    
    private final ChatClient chatClient;

    public AiService(ChatClient chatClient){
         this.chatClient = chatClient;
    }

    public String askgemini(@NonNull String query){
        return this.chatClient
                .prompt(query)
                .call()
                .content();
    }

}
