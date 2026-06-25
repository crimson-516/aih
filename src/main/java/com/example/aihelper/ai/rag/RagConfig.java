package com.example.aihelper.ai.rag;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//加载rag
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class RagConfig {

    // 向量存储（彻底解决 NoSuchBean 异常）
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    // 检索器（无任何循环依赖）
    @Bean
    public ContentRetriever contentRetriever(
            EmbeddingModel qwenEmbeddingModel,
            EmbeddingStore<TextSegment> embeddingStore
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5)
                .minScore(0.75)
                .build();
    }

    // 项目启动后再加载文档（彻底解决循环依赖）
    @Bean
    public CommandLineRunner ragInit(
            EmbeddingModel qwenEmbeddingModel,
            EmbeddingStore<TextSegment> embeddingStore
    ) {
        return args -> {
            var documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
            var splitter = new DocumentByParagraphSplitter(1000, 200);

            EmbeddingStoreIngestor.builder()
                    .documentSplitter(splitter)
                    .textSegmentTransformer(seg -> TextSegment.from(
                            seg.metadata().getString("file_name") + "\n" + seg.text(),
                            seg.metadata()
                    ))
                    .embeddingModel(qwenEmbeddingModel)
                    .embeddingStore(embeddingStore)
                    .build()
                    .ingest(documents);

            System.out.println("✅ RAG 文档向量化完成");
        };
    }
}
