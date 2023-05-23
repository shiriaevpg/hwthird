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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
@Slf4j
public class Worker {
    private final Counter ans;
    private final Counter err;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    Worker(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        err = meterRegistry.counter("errors_worker_total");
        ans = meterRegistry.counter("answers_worker_total");
    }

    @RabbitListener(queues = BrokerConfiguration.TOPIC_NAME1, ackMode = "MANUAL")
    public void processRequest(long attempts, MessageHeaders headers, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws InterruptedException, IOException {
        log.info("\nReceived task: count pi using Monte-Carlo method in  \t" + attempts + " times\n");
        channel.basicAck(tag, false);
        try {
            double calculatedPi = CalculatePi(attempts);
            ans.increment();
            rabbitTemplate.convertAndSend(BrokerConfiguration.TOPIC_NAME2, calculatedPi);
        }  catch (Exception e) {
            err.increment();
            log.info("An error has occurred");
        }
    }

    public void work(long attempts){
        log.info("\nReceived task FROM CONTROLLER: count pi using Monte-Carlo method in  \t" + attempts + " times\n");
        try {
            CalculatePi(attempts);
        } catch (Exception e){
            log.info("An error has occurred");
        }
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
