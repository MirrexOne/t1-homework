package ru.t1.java.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic metricsTopic() {
        return TopicBuilder.name("t1_demo_metrics")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic accountsTopic() {
        return TopicBuilder.name("t1_demo_accounts")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("t1_demo_transactions")
                .partitions(1)
                .replicas(1)
                .build();
    }
}