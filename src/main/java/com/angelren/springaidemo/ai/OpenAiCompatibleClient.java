package com.angelren.springaidemo.ai;

import com.angelren.springaidemo.config.AiProperties;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class OpenAiCompatibleClient implements AiClient {

    private final RestClient restClient;
    private final AiProperties aiProperties;

    public OpenAiCompatibleClient(RestClient restClient, AiProperties aiProperties) {
        this.restClient = restClient;
        this.aiProperties = aiProperties;
    }

    @Override
    public String answerQuestion(String context, String question) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                aiProperties.model(),
                List.of(
                        new ChatMessage(
                                "system",
                                "You are a helpful assistant. Answer only from the provided context. If the answer is not in the context, say so clearly."
                        ),
                        new ChatMessage(
                                "user",
                                "Context:\n" + context + "\n\nQuestion:\n" + question
                        )
                )
        );

        ChatCompletionResponse response = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("AI provider returned an empty response");
        }

        ChatMessage message = response.choices().getFirst().message();
        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new IllegalStateException("AI provider response did not contain a message");
        }

        return message.content();
    }

    public record ChatCompletionRequest(
            String model,
            List<ChatMessage> messages
    ) {
    }

    public record ChatCompletionResponse(List<Choice> choices) {
    }

    public record Choice(ChatMessage message) {
    }

    public record ChatMessage(
            String role,
            String content
    ) {
    }
}
