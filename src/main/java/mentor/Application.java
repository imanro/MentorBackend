package mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}



// Check the checksum of file with strings
// if differs or null:
// Download the particular file from GDrive
// Convert it to csv and store somewhere
// store this checksum in db

// Run this application by cron

