package br.com.el.chatbot.controller;

import br.com.el.chatbot.openAI.OpenAiApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class indexController {

    @PostMapping("/bot")
    public String messageReceiver(@RequestParam("Body") String body) {
        String response;
        try {
            response = OpenAiApi.completePrompt(body);
        } catch (Exception e) {
            e.printStackTrace();
            response = "Erro ao processar a mensagem.";
        }
        return response;
    }

}
