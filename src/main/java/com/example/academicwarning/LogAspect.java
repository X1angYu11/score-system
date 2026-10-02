package com.example.academicwarning;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    @Around("execution(* com.example.academicwarning.*Controller.*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();

        try {
            Object result = pjp.proceed();
            log.info("[接口] {} 成功，耗时 {} ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.warn("[接口] {} 失败（{}），耗时 {} ms", method, e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }
    }
}
