package org.example.apimanage.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流配置类
 * 基于 Bucket4j 实现令牌桶限流
 */
@Slf4j
@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.ai.capacity:10}")
    private int aiCapacity;

    @Value("${rate.limit.ai.refill-rate:1}")
    private int aiRefillRate;

    @Value("${rate.limit.ai.enabled:true}")
    private boolean aiRateLimitEnabled;

    // 存储每个 IP 的限流桶
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * 获取或创建 AI 接口限流桶
     */
    public Bucket getAiBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    /**
     * 创建新的令牌桶
     * capacity: 桶容量（突发流量）
     * refillRate: 每秒补充令牌数
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(aiCapacity, Refill.intervally(aiRefillRate, Duration.ofSeconds(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 检查是否允许请求
     */
    public boolean tryAcquire(String ip) {
        if (!aiRateLimitEnabled) {
            return true;
        }
        Bucket bucket = getAiBucket(ip);
        return bucket.tryConsume(1);
    }

    /**
     * 获取剩余令牌数
     */
    public long getAvailableTokens(String ip) {
        Bucket bucket = getAiBucket(ip);
        return bucket.getAvailableTokens();
    }

    /**
     * 清理过期的桶（可定时任务调用）
     */
    public void clearBuckets() {
        log.info("Clearing rate limit buckets, current size: {}", buckets.size());
        buckets.clear();
    }
}
