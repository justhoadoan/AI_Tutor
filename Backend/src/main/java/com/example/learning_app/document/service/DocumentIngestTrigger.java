package com.example.learning_app.document.service;

import com.example.learning_app.document.model.Document;

public interface DocumentIngestTrigger {
    void triggerIngestion(Document document);
}
