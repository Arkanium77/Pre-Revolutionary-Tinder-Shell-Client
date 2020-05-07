package team.isaz.prerevolutionarytinder.client.shell.services;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import team.isaz.prerevolutionarytinder.client.shell.domain.ClientProfile;
import team.isaz.prerevolutionarytinder.client.shell.domain.Response;
import team.isaz.prerevolutionarytinder.client.shell.domain.URLRepository;
import team.isaz.prerevolutionarytinder.client.shell.utils.StringConvertationUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Простой сервис отправки запросов и парсинга ответов в {@link Response} сущность.
 */
@Slf4j
public class RequestResponseService {
    private final URLRepository url;
    private final RestTemplate restTemplate;

    public RequestResponseService(RestTemplate restTemplate, URLRepository url) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public Map<String, String> getPublicProfileInfo(String uuid) {
        var uri = url.getPublicProfileInfo(uuid);
        var requestEntity = RequestEntity.get(uri).build();
        var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode().equals(HttpStatus.OK))
            return response.getBody();
        return null;
    }

    public Response getNextByOrderUUID(int rowNumber) {
        var uri = url.getNextUserByRowNumber(rowNumber);
        var requestEntity = RequestEntity.get(uri).build();
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            return new Response(false);
        }
    }

    public Response changeProfileMessage(String sessionId, String profileMessage) {
        var uri = url.changeProfileMessage();
        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", sessionId);
        requestParams.put("profile_message", profileMessage);
        var requestEntity = RequestEntity.put(uri).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return new Response(false, e.getMessage());
        }
    }

    public Response createProfile(String username, String password) {
        Map<String, String> u = new HashMap<>();
        u.put("username", username);
        u.put("password", password);
        var uri = url.login();
        var request = RequestEntity.post(uri).body(u);
        try {
            var response = restTemplate.exchange(request, String.class);
            var profile = new ClientProfile(response.getBody(),
                    username, password);
            return new Response(true, profile);
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
    }

    public Response getNextRelatedUUID(String sessionId) {
        var uri = url.getRelatedUUID();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", sessionId);
        var requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            log.debug(e.getMessage());
        }
        return new Response(false, "Нет подходящих анкет");
    }

    public Response getMatchesMap(String sessionId) {
        var uri = url.getAllMatches();
        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", sessionId);
        var requestEntity = RequestEntity.post(uri).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
            });
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            return getErrorResponseByCode(e);
        }
    }

    public Response sendRelation(Boolean relation, String sessionId, String whom) {
        var uri = url.sendRelation();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", sessionId);
        requestParams.put("whom", whom);
        requestParams.put("is_like", relation.toString());

        var requestEntity = RequestEntity.post(uri).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            return getErrorResponseByCode(e);
        }
    }

    private Response getErrorResponseByCode(RestClientException e) {
        if (StringUtils.contains(e.getMessage(), "401")) {
            return new Response(false, "Охъ! Время коннекта вышло! Войдите снова!");
        }
        return new Response(false, "Неизвестная ошибка!\n" + e.getMessage());
    }

    public Response tryRegister(String username, String password, String sex) {
        sex = StringConvertationUtils.sexFromRepresentToBoolean(sex);
        Map<String, String> u = new HashMap<>();
        u.put("username", username);
        u.put("password", password);
        u.put("sex", sex);
        var uri = url.register();
        var request = RequestEntity.post(uri).body(u);
        try {
            var response = restTemplate.exchange(request, String.class);
            return new Response(true, response.getBody());
        } catch (Throwable e) {
            log.debug(e.getMessage());
        }

        return new Response(false, "Неудача");
    }
}
