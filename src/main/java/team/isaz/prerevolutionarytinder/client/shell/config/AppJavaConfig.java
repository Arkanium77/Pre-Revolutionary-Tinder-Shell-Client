package team.isaz.prerevolutionarytinder.client.shell.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;
import team.isaz.prerevolutionarytinder.client.shell.services.URLRepository;

@Configuration
public class AppJavaConfig {

    @Bean
    RequestResponseService requestResponseService() {
        return new RequestResponseService(restTemplate(), urlRepository());
    }

    @Bean
    CommandStatusService commandStatusService() {
        return new CommandStatusService();
    }

    @Bean
    URLRepository urlRepository() {
        return new URLRepository("http://localhost:8080");
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:8080")
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"))
                .build();
    }


}
