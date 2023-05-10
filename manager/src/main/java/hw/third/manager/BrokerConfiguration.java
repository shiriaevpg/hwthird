package hw.third.manager;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {
    public static final String TOPIC_NAME1 = "queue_topic_tasks";
    public static final String TOPIC_NAME2 = "queue_topic_responses";
    @Bean
    public Queue queue1() {
        return new Queue(TOPIC_NAME1, true);
    }

    @Bean
    public Queue queue2() {
        return new Queue(TOPIC_NAME2, true);
    }
}