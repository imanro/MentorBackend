package mentor.mailimport;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/mailimport")
public class ImportController {

    @RequestMapping("/run")
    public ResponseEntity<String> run() {

        System.out.println("Starting import...");

        Importer importer = new Importer();

        if (importer.run()) {
            return ResponseEntity.ok("{ \"message\": \"Successfully created\" }\n");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("");
        }
    }
}
