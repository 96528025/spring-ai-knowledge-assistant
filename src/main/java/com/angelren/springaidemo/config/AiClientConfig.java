package com.angelren.springaidemo.config;

import com.angelren.springaidemo.ai.AiClient;
import com.angelren.springaidemo.ai.MockAiClient;
import com.angelren.springaidemo.ai.OpenAiCompatibleClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    @Bean
    @ConditionalOnMissingBean(AiClient.class)
    public AiClient aiClient(AiProperties aiProperties) {
        if (aiProperties.enabled()) {
            validateProperties(aiProperties);

            RestClient restClient = RestClient.builder()
                    .baseUrl(aiProperties.baseUrl())
                    .build();

            return new OpenAiCompatibleClient(restClient, aiProperties);
        }

        return new MockAiClient();
    }

    private void validateProperties(AiProperties aiProperties) {
        if (isBlank(aiProperties.baseUrl())) {
            throw new IllegalStateException("app.ai.base-url must be configured when app.ai.enabled=true");
        }

        if (isBlank(aiProperties.apiKey())) {
            throw new IllegalStateException("app.ai.api-key must be configured when app.ai.enabled=true");
        }

        if (isBlank(aiProperties.model())) {
            throw new IllegalStateException("app.ai.model must be configured when app.ai.enabled=true");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
