package hw.third.manager;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.micrometer.core.instrument.Counter;

import java.util.Random;

@RestController
@Slf4j
public class ManagerController {
    private final RabbitTemplate rabbitTemplate;

    private final Random random;
    private final Counter tasks;

    @Autowired
    ManagerController(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.random = new Random();
        tasks = meterRegistry.counter("tasks_sender_total");
    }

     @GetMapping("/createTask")
//    @PostMapping("/createManagerTask")
    void createTask(int times) {
        tasks.increment();
        rabbitTemplate.convertAndSend(BrokerConfiguration.TOPIC_NAME1, times);
        log.info("\n");
    }

     @GetMapping("/generateTasks")
//    @PostMapping("/generateTasks")
    public void run(int count, int min_time, int max_time) {
        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < count; ++i) {
            int times = (random.nextInt(max_time / min_time) + 1) * min_time;
            createTask(times);
        }
    }
}
