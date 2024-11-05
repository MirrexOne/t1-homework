package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceError {

    private final DataSourceErrorLogService dataSourceErrorLogService;

    @Around("execution(* ru.t1.java.demo.service.*.*(..))")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            String methodSignature = joinPoint.getSignature().toShortString();
            ru.t1.java.demo.model.DataSourceErrorLog errorLog = new ru.t1.java.demo.model.DataSourceErrorLog();
            errorLog.setStackTrace(ExceptionUtils.getStackTrace(e));
            errorLog.setMessage(e.getMessage());
            errorLog.setMethodSignature(methodSignature);
            dataSourceErrorLogService.createErrorLog(errorLog);
            throw e;
        }
    }
}
