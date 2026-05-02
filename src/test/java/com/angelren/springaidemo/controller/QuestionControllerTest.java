package com.angelren.springaidemo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.angelren.springaidemo.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentService documentService;

    @BeforeEach
    void clearState() {
        documentService.clearDocuments();
    }

    @Test
    void askQuestionReturnsMockAnswerWhenDocumentsExist() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "company.txt",
                "text/plain",
                "Five9 provides cloud contact center software.".getBytes()
        );

        mockMvc.perform(multipart("/api/documents/upload").file(file))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/questions/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "What does Five9 do?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("What does Five9 do?"))
                .andExpect(jsonPath("$.answer").value("Mock answer based on uploaded documents. Question: What does Five9 do?"));
    }

    @Test
    void askQuestionReturnsBadRequestForBlankQuestion() throws Exception {
        mockMvc.perform(post("/api/questions/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Question must not be blank"));
    }
}
