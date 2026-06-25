package com.example.aihelper.ai.service;

import dev.langchain4j.service.SystemMessage;
import java.util.List;


public interface AiCodeHelperService {

    @SystemMessage(fromResource = "System-prompt.txt")
    String chat(String userMessage);
    Report chatForReport(String userMessage);
    record Report(String name, List<String> sugestionList){}
}
