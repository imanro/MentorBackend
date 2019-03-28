package hello;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/mailimport/run")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        HashMap<String, String> map = new HashMap<>();

        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
