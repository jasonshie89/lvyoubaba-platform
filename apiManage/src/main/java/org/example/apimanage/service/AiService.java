package org.example.apimanage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * AI 服务类 - OpenAI 兼容 API 格式
 * 支持：OpenAI、Claude、DeepSeek、MiMo、SiliconFlow 等
 */
@Slf4j
@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.model:gpt-4o}")
    private String model;

    public AiService(@Value("${ai.api.key}") String apiKey,
                     @Value("${ai.api.base-url}") String baseUrl,
                     @Value("${ai.api.timeout:30000}") int timeout) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
        log.info("AI Service initialized with baseUrl: {}, model: {}", baseUrl, model);
    }

    /**
     * 生成行程安排
     *
     * @param destination   目的地
     * @param duration      天数
     * @param difficulty    难度等级 1-3
     * @param maxMembers    最大人数
     * @param budget        预算
     * @return 生成的行程文案
     */
    public Mono<String> generateItinerary(String destination, Integer duration, Integer difficulty,
                                          Integer maxMembers, String budget) {
        String prompt = buildItineraryPrompt(destination, duration, difficulty, maxMembers, budget);
        return chatCompletion(prompt);
    }

    /**
     * 智能提取标签
     *
     * @param content 内容文本
     * @return 标签列表
     */
    public Mono<List<String>> extractTags(String content) {
        String prompt = "请从以下旅行动态中提取3-5个关键词标签，用逗号分隔，只返回标签本身：\n" + content;
        return chatCompletion(prompt)
                .map(result -> List.of(result.split("，|,")))
                .map(tags -> tags.stream()
                        .map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .limit(5)
                        .toList());
    }

    /**
     * 内容审核
     *
     * @param content 待审核内容
     * @return 审核结果
     */
    public Mono<ContentCheckResult> checkContent(String content) {
        String prompt = "请审核以下旅游平台内容，判断是否有违规内容（色情、暴力、政治敏感、广告等），" +
                "返回JSON格式：{\"isValid\": true/false, \"reason\": \"原因\"}\n\n内容：" + content;

        return chatCompletion(prompt)
                .map(result -> {
                    try {
                        JsonNode node = objectMapper.readTree(result);
                        return new ContentCheckResult(
                                node.get("isValid").asBoolean(true),
                                node.has("reason") ? node.get("reason").asText() : null
                        );
                    } catch (Exception e) {
                        log.warn("AI 审核结果解析失败，默认通过: {}", result);
                        return new ContentCheckResult(true, null);
                    }
                });
    }

    /**
     * 通用 Chat Completion 接口（OpenAI 兼容格式）
     */
    private Mono<String> chatCompletion(String userMessage) {
        return chatCompletion(userMessage, 0.7, 2000);
    }

    /**
     * 通用 Chat Completion 接口（OpenAI 兼容格式）
     *
     * @param userMessage 用户消息
     * @param temperature 温度参数 0-2
     * @param maxTokens   最大 token 数
     * @return AI 回复内容
     */
    private Mono<String> chatCompletion(String userMessage, double temperature, int maxTokens) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);

        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", userMessage);

        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofMillis(maxTokens > 4000 ? 60000 : 30000))
                .map(response -> {
                    try {
                        // OpenAI 标准响应格式
                        return response.get("choices").get(0).get("message").get("content").asText();
                    } catch (Exception e) {
                        log.error("解析 AI 响应失败: {}", response);
                        throw new RuntimeException("AI 响应解析失败: " + e.getMessage());
                    }
                })
                .doOnError(e -> log.error("调用 AI API 失败: {}", e.getMessage()));
    }

    /**
     * 流式 Chat Completion（用于打字机效果）
     */
    public Mono<String> chatCompletionStream(String userMessage) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);

        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", userMessage);

        requestBody.put("temperature", 0.7);
        requestBody.put("stream", true);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .map(lines -> String.join("", lines));
    }

    private String buildItineraryPrompt(String destination, Integer duration, Integer difficulty,
                                        Integer maxMembers, String budget) {
        String difficultyText = switch (difficulty) {
            case 1 -> "轻松休闲";
            case 2 -> "适中挑战";
            case 3 -> "高强度";
            default -> "适中";
        };

        return String.format("""
                请为以下约伴活动生成一份专业的行程安排：

                【基本信息】
                - 目的地：%s
                - 天数：%d天
                - 难度：%s
                - 人数：%d人
                - 预算：%s元/人

                请生成包含以下要素的行程描述：
                1. 每日行程安排（时间、地点、活动）
                2. 交通方式建议
                3. 住宿建议
                4. 餐饮推荐
                5. 注意事项和安全提醒
                6. 装备建议

                要求：
                - 语言简洁专业
                - 适合户外驴友群体
                - 字数控制在300-500字
                - 使用友好亲切的语气
                """, destination, duration, difficultyText, maxMembers, budget);
    }

    public record ContentCheckResult(boolean isValid, String reason) {}
}
