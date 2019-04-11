package mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ComponentScan
public class ApiApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        System.out.println("Test");
        SpringApplication.run(ApiApplication.class, args);
    }
}
