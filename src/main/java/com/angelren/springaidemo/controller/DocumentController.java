package com.angelren.springaidemo.controller;

import com.angelren.springaidemo.model.DocumentRecord;
import com.angelren.springaidemo.service.DocumentService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Uploaded file must not be empty"
            ));
        }

        DocumentRecord savedDocument = documentService.saveDocument(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", savedDocument.id(),
                "fileName", savedDocument.fileName(),
                "uploadedAt", savedDocument.uploadedAt().toString()
        ));
    }

    @GetMapping
    public List<DocumentRecord> listDocuments() {
        return documentService.listDocuments();
    }
}
