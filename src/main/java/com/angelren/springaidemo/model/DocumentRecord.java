package com.angelren.springaidemo.model;

import java.time.Instant;

public record DocumentRecord(
        String id,
        String fileName,
        String content,
        Instant uploadedAt
) {
}
