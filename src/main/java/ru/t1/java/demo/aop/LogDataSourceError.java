package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceError {

    private final DataSourceErrorLogService dataSourceErrorLogService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "t1_demo_metrics";

    @Around("execution(* ru.t1.java.demo.service.*.*(..))")
    public Object logDataSourceError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            String methodSignature = joinPoint.getSignature().toShortString();
            String errorMessage = String.format("Method: %s, Error: %s", methodSignature, e.getMessage());

            Message<String> message = MessageBuilder
                .withPayload(errorMessage)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("errorType", "DATA_SOURCE")
                .build();

            try {
                kafkaTemplate.send(message);
            } catch (Exception kafkaException) {
                DataSourceErrorLog errorLog = new DataSourceErrorLog();
                errorLog.setStackTrace(ExceptionUtils.getStackTrace(e));
                errorLog.setMessage(e.getMessage());
                errorLog.setMethodSignature(methodSignature);
                dataSourceErrorLogService.createErrorLog(errorLog);
            }
            throw e;
        }
    }
}
