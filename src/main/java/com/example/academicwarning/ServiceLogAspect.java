
package com.example.academicwarning;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import org.slf4j.Logger;
@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log= LoggerFactory.getLogger(ServiceLogAspect.class);
    @Around("execution(* com.example.academicwarning.*ServiceImpl.*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        try{
            Object result=pjp.proceed();
            long cost=System.currentTimeMillis() - start;
            log.info("[Service] {} 参数={} 耗时 {} ms", method, Arrays.toString(args), cost);
            return result;
        }catch(Throwable e){
            log.warn("[Service] {} 参数={} 异常 {}", method, Arrays.toString(args), e.getMessage(), e);
            throw e;
        }
    }
}
