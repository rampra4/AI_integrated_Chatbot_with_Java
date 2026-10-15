
package com.gemini.AiInte.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import com.gemini.AiInte.Services.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/ai")
public class AiConroller {

    private final AiService aiService;
    public AiConroller(AiService aiService){
         this.aiService = aiService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> askgemini(@RequestParam("q") @NonNull String query){
        return ResponseEntity.ok(aiService.askgemini(query));
    }    
}