package team.isaz.prerevolutionarytinder.client.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class PreRevolutionaryTinderClientApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PreRevolutionaryTinderClientApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8083"));
        app.run(args);
        //SpringApplication.run(PreRevolutionaryTinderClientApplication.class, args);
    }

}
