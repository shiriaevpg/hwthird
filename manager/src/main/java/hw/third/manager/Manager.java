package hw.third.manager;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class Manager {
    @RabbitListener(queues = BrokerConfiguration.TOPIC_NAME2, ackMode = "MANUAL")
    public void processResponse(double counted_pi, MessageHeaders headers, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        channel.basicAck(tag, false);
        log.info("\nManager received successfully completed task. Computed value of pi:\t" + counted_pi + "\n");
    }
}
