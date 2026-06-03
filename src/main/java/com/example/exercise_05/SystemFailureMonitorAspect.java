package com.example.exercise_05;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j // Khởi tạo Logger
public class SystemFailureMonitorAspect {

    // Bắt toàn bộ lỗi văng ra từ OrderService
    // Lưu ý: Đổi tên package com.example.service cho khớp với dự án của bạn
    @AfterThrowing(pointcut = "execution(* com.example..OrderService.*(..))", throwing = "ex")
    public void monitorSystemFailures(JoinPoint joinPoint, Throwable ex) {
        try {
            // 1. Gắn requestId bằng UUID để truy vết
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);

            // 2. In log lỗi 1 lần duy nhất ở đây (các tầng khác tuyệt đối không try-catch in log nữa)
            String methodName = joinPoint.getSignature().getName();
            log.error("Hệ thống gặp sự cố tại hàm [{}]. Chi tiết lỗi: {}", methodName, ex.getMessage(), ex);

        } finally {
            // 3. Bắt buộc phải clear() MDC để tránh rò rỉ bộ nhớ (Memory Leak) cho các Thread khác
            MDC.clear();
        }
    }
}
