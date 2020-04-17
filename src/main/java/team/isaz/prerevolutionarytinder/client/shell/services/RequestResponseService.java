package team.isaz.prerevolutionarytinder.client.shell.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import team.isaz.prerevolutionarytinder.client.shell.StringConvertationUtils;
import team.isaz.prerevolutionarytinder.client.shell.domain.ClientProfile;
import team.isaz.prerevolutionarytinder.client.shell.domain.Response;

import java.util.*;
import java.util.stream.Collectors;

public class RequestResponseService {
    Logger logger = LoggerFactory.getLogger(RequestResponseService.class);
    ClientProfile profile;
    int rowNumber;
    RestTemplate restTemplate;
    Map<String, String> matches;
    URLRepository url;

    public RequestResponseService(RestTemplate restTemplate, URLRepository url) {
        profile = null;
        matches = null;
        this.restTemplate = restTemplate;
        rowNumber = 0;
        this.url = url;
    }

    public String like() {
        if (profile == null) return "Любовь проявлена";

        return sendRelation(true);
    }

    private String sendRelation(Boolean relation) {
        var uri = url.sendRelation();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        requestParams.put("whom", profile.getCurrentProfile().toString());
        requestParams.put("is_like", relation.toString());

        var requestEntity = new RequestEntity<Map<String, String>>(requestParams, HttpMethod.POST, uri);
        var response = restTemplate.exchange(requestEntity, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) return response.getBody();
        return "Ошибка! Попробуйте ещё разъ!";
    }

    public String showNext() {
        String nextProfileUUID = getNextUUID();
        var map = Objects.requireNonNull(getPublicProfileInfo(nextProfileUUID));
        return createProfileView(map.get("username"), map.get("sex"), map.get("profile_message"));
    }

    private String createProfileView(String username, String sex, String profile_message) {
        sex = StringConvertationUtils.sexFromBooleanToRepresent(sex);
        var list = lineBreakers(profile_message, 50);
        list.add(StringUtils.leftPad(sex, 50));
        list.add(StringUtils.leftPad(username, 50));
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.repeat('#', 54)).append('\n');
        list.forEach(s -> builder.append("# ").append(s).append(" #").append('\n'));
        builder.append(StringUtils.repeat('#', 54)).append('\n');
        return builder.toString();
    }

    private List<String> lineBreakers(String profile_message, int maxStringLength) {
        var base = profile_message.split(" ");
        var result = new ArrayList<String>();
        StringBuilder string = new StringBuilder();
        for (String s : base) {
            if ((string.length() + s.length()) > maxStringLength) {
                result.add(string.toString());
                string = new StringBuilder();
            }
            string.append(s).append(' ');
        }
        if (string.length() != 0) result.add(string.toString());


        return result.stream()
                .map(s -> StringUtils.rightPad(s, 50))
                .collect(Collectors.toList());

    }

    private Map<String, String> getMatchesMap() {
        var uri = url.getAllMatches();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        var requestEntity = new RequestEntity<Map<String, String>>(requestParams, HttpMethod.GET, uri);
        var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode().equals(HttpStatus.OK)) return response.getBody();
        return null;
    }

    private String getNextRelatedUUID() {
        var uri = url.getRelatedUUID();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        //var requestEntity = new RequestEntity<>(requestParams, HttpMethod.GET, uri);
        var requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            System.out.println(e);
        }
        return null;
    }

    private String getNextUUID() {
        if (profile == null) return getNextByOrderUUID();
        return getNextRelatedUUID();
    }

    private Map<String, String> getPublicProfileInfo(String uuid) {
        var uri = url.getPublicProfileInfo(uuid);

        var requestEntity = new RequestEntity<String>(HttpMethod.GET, uri);
        var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode().equals(HttpStatus.OK))
            return response.getBody();
        return null;
    }

    private String getNextByOrderUUID() {
        var uri = url.getNextUserByRowNumber(rowNumber);
        var requestEntity = new RequestEntity<String>(HttpMethod.GET, uri);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            rowNumber++;
            return response.getBody();
        } catch (RestClientException e) {
            rowNumber = 0;
        }
        return getNextByOrderUUID();
    }

    public String dislike() {
        if (profile == null) return "Видно не судьба, видно нѣтъ любви.";

        return sendRelation(false);
    }

    public Response register(String username, String password, String sex, String profileMessage) {
        String registerResponse = tryRegister(username, password, sex);
        if (!StringConvertationUtils.isThatUUID(registerResponse))
            return new Response(false, "Неудача, попробуйте снова!");

        profile = new ClientProfile(UUID.fromString(Objects.requireNonNull(registerResponse)),
                username, password, Boolean.parseBoolean(sex));

        UUID currentUUID = UUID.fromString(getNextUUID());
        profile.setCurrentProfile(currentUUID);
        if (!profileMessage.equals("")) changeProfileMessage(profileMessage);
        return new Response(true, "Успехъ!\n\n" + showNext());
    }

    private void changeProfileMessage(String profileMessage) {
        var uri = url.changeProfileMessage();
        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        requestParams.put("profile_message", profileMessage);
        var requestEntity = new RequestEntity<Map<String, String>>(requestParams, HttpMethod.PUT, uri);
        var response = restTemplate.exchange(requestEntity, String.class);
        logger.debug("profile message change statis is {}\nattached string: {}",
                response.getStatusCode(), response.getBody());
    }

    private String tryRegister(String username, String password, String sex) {
        sex = StringConvertationUtils.sexFromRepresentToBoolean(sex);
        Map<String, String> u = new HashMap<>();
        u.put("username", username);
        u.put("password", password);
        u.put("sex", sex);
        var uri = url.register();
        var request = new RequestEntity<>(u, HttpMethod.POST, uri);
        try {
            var response = restTemplate.exchange(request, String.class);
            return response.getBody();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Response login(String username, String password) {
        Map<String, String> u = new HashMap<>();
        u.put("username", username);
        u.put("password", password);
        var uri = url.login();
        var request = new RequestEntity<>(u, HttpMethod.POST, uri);
        var response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            UUID currentUUID = UUID.fromString(getNextUUID());
            profile.setCurrentProfile(currentUUID);

            return new Response(true, "Успехъ!\n\n" +
                    getPublicProfileInfo(profile.getCurrentProfile().toString()));
        }
        return new Response(false, "Неудача, попробуйте снова!");
    }

    public String showAll() {
        matches = getMatchesMap();
        if (matches == null) return "Невозможно отобразить списокъ любимцевъ";
        StringBuilder builder = new StringBuilder("Списокъ Любимцевъ:\n");
        var array = matches.values().toArray();
        for (int i = 0; i < array.length; i++) {
            builder
                    .append(i + 1)
                    .append(") ")
                    .append(array[i]);
        }
        return builder.toString();
    }

    public String getMatchProfile(int number) {
        if (matches == null) return "Обновляем список любимцев.\n\n" + showAll() + "\nПопробуйте ещё!";
        if (number > matches.size() || number < 0) return "Нет такого номера!";
        var uuid = matches.keySet().toArray()[number].toString();
        var map = Objects.requireNonNull(getPublicProfileInfo(uuid));
        return createProfileView(map.get("username"), map.get("sex"), map.get("profile_message"));
    }
}
