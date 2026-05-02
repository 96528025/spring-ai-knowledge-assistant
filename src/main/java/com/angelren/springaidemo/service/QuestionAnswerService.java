package com.angelren.springaidemo.service;

import com.angelren.springaidemo.ai.AiClient;
import com.angelren.springaidemo.dto.AskQuestionRequest;
import com.angelren.springaidemo.model.DocumentRecord;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerService {

    private final DocumentService documentService;
    private final AiClient aiClient;

    public QuestionAnswerService(DocumentService documentService, AiClient aiClient) {
        this.documentService = documentService;
        this.aiClient = aiClient;
    }

    public String answerQuestion(AskQuestionRequest request) {
        List<DocumentRecord> documents = documentService.listDocuments();
        String context = documents.stream()
                .map(DocumentRecord::content)
                .reduce("", (left, right) -> left + "\n\n" + right)
                .trim();

        return aiClient.answerQuestion(context, request.question());
    }
}
