package org.example.apimanage.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apimanage.dto.R;
import org.example.apimanage.service.AiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * AI 接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 生成行程安排
     */
    @PostMapping("/generate-itinerary")
    public Mono<R<String>> generateItinerary(@RequestBody GenerateItineraryRequest request) {
        log.info("生成行程安排: destination={}, duration={}", request.getDestination(), request.getDuration());
        return aiService.generateItinerary(
                        request.getDestination(),
                        request.getDuration(),
                        request.getDifficulty(),
                        request.getMaxMembers(),
                        request.getBudget()
                )
                .map(R::success)
                .onErrorResume(e -> {
                    log.error("生成行程失败: {}", e.getMessage());
                    return Mono.just(R.error("AI 生成失败: " + e.getMessage()));
                });
    }

    /**
     * 智能提取标签
     */
    @PostMapping("/extract-tags")
    public Mono<R<List<String>>> extractTags(@RequestBody ExtractTagsRequest request) {
        log.info("提取标签: content length={}", request.getContent().length());
        return aiService.extractTags(request.getContent())
                .map(R::success)
                .onErrorResume(e -> {
                    log.error("提取标签失败: {}", e.getMessage());
                    return Mono.just(R.error("标签提取失败"));
                });
    }

    /**
     * 内容审核
     */
    @PostMapping("/check-content")
    public Mono<R<ContentCheckResponse>> checkContent(@RequestBody CheckContentRequest request) {
        log.info("内容审核: content length={}", request.getContent().length());
        return aiService.checkContent(request.getContent())
                .map(result -> {
                    ContentCheckResponse response = new ContentCheckResponse();
                    response.setValid(result.isValid());
                    response.setReason(result.reason());
                    return R.success(response);
                })
                .onErrorResume(e -> {
                    log.error("内容审核失败: {}", e.getMessage());
                    return Mono.just(R.error("审核失败"));
                });
    }

    // ========== DTOs ==========

    @Data
    public static class GenerateItineraryRequest {
        private String destination;
        private Integer duration;
        private Integer difficulty;
        private Integer maxMembers;
        private String budget;
    }

    @Data
    public static class ExtractTagsRequest {
        private String content;
    }

    @Data
    public static class CheckContentRequest {
        private String content;
    }

    @Data
    public static class ContentCheckResponse {
        private boolean valid;
        private String reason;
    }
}
