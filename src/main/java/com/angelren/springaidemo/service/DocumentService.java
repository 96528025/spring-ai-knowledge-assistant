package com.angelren.springaidemo.service;

import com.angelren.springaidemo.model.DocumentRecord;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final List<DocumentRecord> documents = new ArrayList<>();

    public DocumentRecord saveDocument(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        DocumentRecord document = new DocumentRecord(
                UUID.randomUUID().toString(),
                file.getOriginalFilename(),
                content,
                Instant.now()
        );

        documents.add(document);
        return document;
    }

    public List<DocumentRecord> listDocuments() {
        return List.copyOf(documents);
    }

    public void clearDocuments() {
        documents.clear();
    }
}
