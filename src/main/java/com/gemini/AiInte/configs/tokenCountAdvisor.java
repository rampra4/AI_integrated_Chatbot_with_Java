package com.gemini.AiInte.configs;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;


import reactor.core.publisher.Flux;

public class tokenCountAdvisor implements CallAdvisor , StreamAdvisor{

    private Logger logger = LoggerFactory.getLogger(tokenCountAdvisor.class);

    @Override
    public @NonNull String getName() {
        return this.getClass().getName()+"_TokenCountAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public @NonNull Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest chatClientRequest,
        @NonNull StreamAdvisorChain streamAdvisorChain) {

            Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
            return chatClientResponseFlux;

    }

    
    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {
    logger.info("Token Count Advisor Invoked");
    logger.info("Request: " + chatClientRequest.prompt().getContents());

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

    logger.info("Response received from the AI model");

        // Defensive null-safe logging: chatClientResponse or nested values may be null
        Optional.ofNullable(chatClientResponse)
            .map(cr -> cr.chatResponse())
            .ifPresent(chatResp -> {
                Optional.ofNullable(chatResp.getResult())
                    .map(result -> result.getOutput())
                    .ifPresent(output -> logger.info("Response: " + output.getText()));

                Optional.ofNullable(chatResp.getMetadata())
                    .map(metadata -> metadata.getUsage())
                    .ifPresent(usage -> logger.info("Token Count Used: " + usage.getTotalTokens()));
            });

        return chatClientResponse;
    }
    
}
