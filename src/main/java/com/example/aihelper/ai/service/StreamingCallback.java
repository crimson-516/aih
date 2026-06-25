package com.example.aihelper.ai.service;

public interface StreamingCallback {
    void onToken(String token);

    void onComplete(String fullText);

    void onError(Throwable error);
}
