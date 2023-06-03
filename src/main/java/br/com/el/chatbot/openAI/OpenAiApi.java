package br.com.el.chatbot.openAI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;

public class OpenAiApi {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // variavel de ambiente necessaria para a requisicao
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public static class OpenAIRequest {
        String model;
        List<Message> messages;
        int max_tokens;
        double temperature;

        public OpenAIRequest(String model, List<Message> messages, int max_tokens, double temperature) {
            this.model = model;
            this.messages = messages;
            this.max_tokens = max_tokens;
            this.temperature = temperature;
        }
    }

    public static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static String completePrompt(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OPENAI_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);

            Gson gson = new Gson();

            Message message = new Message("user", removeAccents(prompt));
            OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo", Collections.singletonList(message), 150, 0.1);
            StringEntity input = new StringEntity(gson.toJson(request));
            post.setEntity(input);

            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if(jsonObject.has("choices")) {
                return jsonObject.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
            } else {
                return "Resposta não encontrada";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String removeAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
