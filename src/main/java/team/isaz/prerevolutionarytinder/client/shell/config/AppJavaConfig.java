package team.isaz.prerevolutionarytinder.client.shell.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;

@Configuration
public class AppJavaConfig {

    @Bean
    RequestResponseService requestResponseService() {
        return new RequestResponseService();
    }
}
