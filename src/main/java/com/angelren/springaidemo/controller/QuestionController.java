package com.angelren.springaidemo.controller;

import com.angelren.springaidemo.dto.AskQuestionRequest;
import com.angelren.springaidemo.service.QuestionAnswerService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionAnswerService questionAnswerService;

    public QuestionController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askQuestion(@RequestBody AskQuestionRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Question must not be blank"
            ));
        }

        String answer = questionAnswerService.answerQuestion(request);
        return ResponseEntity.ok(Map.of(
                "question", request.question(),
                "answer", answer
        ));
    }
}
