package team.isaz.prerevolutionarytinder.client.shell.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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

        var relationResponse = sendRelation(true);

        return getCheckedByAuthorizeString(relationResponse);
    }

    private Response sendRelation(Boolean relation) {
        var uri = url.sendRelation();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        requestParams.put("whom", profile.getCurrentProfile());
        requestParams.put("is_like", relation.toString());

        var requestEntity = RequestEntity.post(uri).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response);
        } catch (RestClientException e) {
            logger.debug(e.getMessage());
            return new Response(false, e.getMessage());
        }
    }

    public String showNext() {
        var response = getNextUUID();
        if (!response.isStatus()) return "Нет подходящих анкет :(";
        profile.setCurrentProfile(response.getAttach().toString());
        return showProfileById(profile.getCurrentProfile());
    }

    public String showProfileById(String nextProfileUUID) {
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
        var requestEntity = RequestEntity.post(uri).body(requestParams);
        var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode().equals(HttpStatus.OK)) return response.getBody();
        return null;
    }

    private Response getNextRelatedUUID() {
        var uri = url.getRelatedUUID();

        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        var requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(requestParams);
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            logger.debug(e.getMessage());
        }
        return new Response(false, "Нет подходящих анкет");
    }

    private Response getNextUUID() {
        if (profile == null) return getNextByOrderUUID();
        return getNextRelatedUUID();
    }

    private Map<String, String> getPublicProfileInfo(String uuid) {
        var uri = url.getPublicProfileInfo(uuid);

        var requestEntity = RequestEntity.get(uri).build();
        var response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode().equals(HttpStatus.OK))
            return response.getBody();
        return null;
    }

    private Response getNextByOrderUUID() {
        var uri = url.getNextUserByRowNumber(rowNumber);
        var requestEntity = RequestEntity.get(uri).build();
        try {
            var response = restTemplate.exchange(requestEntity, String.class);
            rowNumber++;
            return new Response(true, response.getBody());
        } catch (RestClientException e) {
            rowNumber = 0;
        }
        return getNextByOrderUUID();
    }

    public String dislike() {
        if (profile == null) return "Видно не судьба, видно нѣтъ любви.";

        var relationResponse = sendRelation(false);

        return getCheckedByAuthorizeString(relationResponse);
    }

    private String getCheckedByAuthorizeString(Response relationResponse) {
        if (!relationResponse.isStatus()) return relationResponse.getAttach().toString();
        var responseEntity = (ResponseEntity<String>) relationResponse.getAttach();
        if (responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
            return "Охъ! Время коннекта вышло! Войдите снова!";
        return responseEntity.getBody();
    }

    public Response register(String username, String password, String sex, String profileMessage) {
        var registerResponse = tryRegister(username, password, sex);
        if (!registerResponse.isStatus())
            return new Response(false, "Неудача, попробуйте снова!");

        profile = new ClientProfile(UUID.fromString(registerResponse.getAttach().toString()),
                username, password);
        var response = getNextUUID();
        if (!response.isStatus()) return new Response(true, "Успехъ!\n\nНо анкетъ пока нетъ.");
        String nextProfileUUID = response.getAttach().toString();
        profile.setCurrentProfile(nextProfileUUID);
        if (!profileMessage.equals("")) changeProfileMessage(profileMessage);
        return new Response(true, "Успехъ!\n\n" + showProfileById(profile.getCurrentProfile()));
    }

    private void changeProfileMessage(String profileMessage) {
        var uri = url.changeProfileMessage();
        var requestParams = new HashMap<String, String>();
        requestParams.put("session_id", profile.getSessionId().toString());
        requestParams.put("profile_message", profileMessage);
        var requestEntity = RequestEntity.put(uri).body(requestParams);
        var response = restTemplate.exchange(requestEntity, String.class);
        logger.debug("profile message change statis is {}\nattached string: {}",
                response.getStatusCode(), response.getBody());
    }

    private Response tryRegister(String username, String password, String sex) {
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
            logger.debug(e.getMessage());
        }

        return new Response(false, "Неудача");
    }

    public Response login(String username, String password) {
        Map<String, String> u = new HashMap<>();
        u.put("username", username);
        u.put("password", password);
        var uri = url.login();
        var request = RequestEntity.post(uri).body(u);
        var response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            profile = new ClientProfile(UUID.fromString(Objects.requireNonNull(response.getBody())),
                    username, password);
            var response1 = getNextUUID();
            if (!response1.isStatus()) return new Response(true, "Успехъ!\n\nНо анкетъ пока нетъ.");
            String nextProfileUUID = response1.getAttach().toString();
            profile.setCurrentProfile(nextProfileUUID);

            return new Response(true, "Успехъ!\n\n" +
                    showProfileById(profile.getCurrentProfile()));
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
        if (number > matches.size() || number < 1) return "Нет такого номера!";
        var uuid = matches.keySet().toArray()[number - 1].toString();
        return showProfileById(uuid);
    }
}
