package mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

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

