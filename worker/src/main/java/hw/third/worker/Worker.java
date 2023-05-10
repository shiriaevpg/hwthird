package hw.third.worker;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
@Slf4j
public class Worker {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    Worker(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = BrokerConfiguration.TOPIC_NAME1, ackMode = "MANUAL")
    public void processRequest(long attempts, MessageHeaders headers, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws InterruptedException, IOException {
        log.info("\nReceived task: count pi using Monte-Carlo method in  \t" + attempts + " times\n");
        double calculatedPi = CalculatePi(attempts);
        channel.basicAck(tag, false);
        rabbitTemplate.convertAndSend(BrokerConfiguration.TOPIC_NAME2, calculatedPi);
    }

    private double CalculatePi(long attempts){
        Random r = new Random();
        long successattempts = 0;
        for(long i = 0; i < attempts; ++i){
            double x =  r.nextDouble();
            double y = r.nextDouble();
            if (x*x + y*y <= 1){
                successattempts++;
            }
        }
        return 4*((double)successattempts / attempts);
    }
}
