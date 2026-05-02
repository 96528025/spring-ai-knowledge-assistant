package com.angelren.springaidemo.ai;

public class MockAiClient implements AiClient {

    @Override
    public String answerQuestion(String context, String question) {
        if (context == null || context.isBlank()) {
            return "I do not have any uploaded documents to answer from yet.";
        }

        return "Mock answer based on uploaded documents. Question: " + question;
    }
}
