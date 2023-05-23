package hw.third.worker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Slf4j
public class Controller {
    private final Random random;
    Worker worker;
    @Autowired
    Controller(Worker worker){
        random = new Random();
        this.worker = worker;
    }

    @PostMapping("/randomcreatebyworker")
    void randomcreate(Long number) {
        for (int i = 0; i < number;i++) {
            Long value = random.nextLong(1000);
            worker.work(value);
        }
    }

    @PostMapping("/createTaskbyworker")
    void createTask(Long number) {
        worker.work(number);
    }
}
