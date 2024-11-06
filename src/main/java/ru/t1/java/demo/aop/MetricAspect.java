package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "t1_demo_metrics";

    @Around("@annotation(metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > metric.value()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            String params = Arrays.toString(joinPoint.getArgs());
            
            String metricMessage = String.format(
                "Method: %s, Execution time: %d ms, Parameters: %s",
                methodName, executionTime, params
            );

            Message<String> message = MessageBuilder
                .withPayload(metricMessage)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("errorType", "METRICS")
                .build();

            kafkaTemplate.send(message);
        }

        return result;
    }
}
