package org.example.apimanage.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 操作日志审计切面
 * 记录所有 Controller 方法的调用情况
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切入点：所有 Controller 类的方法
     */
    @Pointcut("execution(* org.example.apimanage.controller.*.*(..))")
    public void controllerPointcut() {}

    /**
     * 环绕通知：记录操作日志
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        LocalDateTime startDateTime = LocalDateTime.now();

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return arg != null ? arg.toString() : "null";
                    }
                })
                .collect(Collectors.joining(", "));

        // 限制参数长度
        if (params.length() > 1000) {
            params = params.substring(0, 1000) + "...";
        }

        // 获取 IP 地址
        String ip = request != null ? getClientIp(request) : "unknown";

        // 获取请求路径
        String uri = request != null ? request.getRequestURI() : "unknown";
        String httpMethod = request != null ? request.getMethod() : "unknown";

        // 记录操作开始日志
        log.info("[AUDIT] [{}] {} {} - {}.{} - IP: {} - Params: {}",
                startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                httpMethod, uri, className, methodName, ip, params);

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();

            // 计算耗时
            long duration = System.currentTimeMillis() - startTime;

            // 记录成功日志
            log.info("[AUDIT] [{}] {} {} - {}.{} - IP: {} - Success - Duration: {}ms",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    httpMethod, uri, className, methodName, ip, duration);

            return result;
        } catch (Exception e) {
            // 计算耗时
            long duration = System.currentTimeMillis() - startTime;

            // 记录失败日志
            log.error("[AUDIT] [{}] {} {} - {}.{} - IP: {} - Failed: {} - Duration: {}ms",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    httpMethod, uri, className, methodName, ip, e.getMessage(), duration);

            throw e;
        }
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
