package team.isaz.prerevolutionarytinder.client.shell.services;

import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.isaz.prerevolutionarytinder.client.shell.domain.ClientProfile;
import team.isaz.prerevolutionarytinder.client.shell.domain.Response;
import team.isaz.prerevolutionarytinder.client.shell.services.helper.ViewGenerator;

import java.util.Map;
import java.util.Objects;

public class CommandHandlerService {
    private final RequestResponseService requestResponseService;
    private final Logger logger = LoggerFactory.getLogger(CommandHandlerService.class);
    private final ViewGenerator viewGenerator;

    private ClientProfile profile;
    private int rowNumber;
    private Map<String, String> matches;

    public CommandHandlerService(ViewGenerator viewGenerator, RequestResponseService requestResponseService) {
        profile = null;
        matches = null;
        rowNumber = 0;
        this.viewGenerator = viewGenerator;
        this.requestResponseService = requestResponseService;
    }


    public Response like() {
        if (profile == null) return new Response(true, "Любовь проявлена");

        return requestResponseService
                .sendRelation(
                        true,
                        profile.getSessionId(),
                        profile.getCurrentProfile());
    }

    public Response showNext() {
        var response = getNextUUID();
        if (!response.isStatus()) return new Response(false, "Нет подходящих анкет :(");
        if (profile != null) profile.setCurrentProfile(response.getAttach().toString());
        return new Response(true, showProfileById(response.getAttach().toString()));
    }

    public Response dislike() {
        if (profile == null) return new Response(true, "Видно не судьба, видно нѣтъ любви.");

        return requestResponseService
                .sendRelation(
                        false,
                        profile.getSessionId(),
                        profile.getCurrentProfile());
    }

    public Response register(String username, String password, String sex, String profileMessage) {
        var registerResponse = requestResponseService.tryRegister(username, password, sex);
        if (!registerResponse.isStatus()) {
            profile = null;
            return new Response(false, "Неудача, попробуйте снова!");
        }
        var s = "Успех!\n";
        profile = new ClientProfile(registerResponse.getAttach().toString(), username, password);
        s = changeProfileMessage(profileMessage, s);
        if (setupCurrentProfileIsFail()) return new Response(true, s + "\nНо анкетъ пока нетъ.");

        return new Response(true, s + "\n" + showProfileById(profile.getCurrentProfile()));
    }


    public Response login(String username, String password) {
        var mayBeProfile = requestResponseService.createProfile(username, password);
        if (!mayBeProfile.isStatus()) {
            profile = null;
            return mayBeProfile;
        }
        profile = (ClientProfile) mayBeProfile.getAttach();
        if (setupCurrentProfileIsFail()) return new Response(true, "Успехъ!\n\nНо анкетъ пока нетъ.");

        return new Response(true, "Успехъ!\n\n" +
                showProfileById(profile.getCurrentProfile()));
    }


    public Response showAll() {
        var mayBeMatches = requestResponseService.getMatchesMap(profile.getSessionId());

        if (!mayBeMatches.isStatus())
            return mayBeMatches;
        matches = (Map<String, String>) mayBeMatches.getAttach();

        var response = viewGenerator.createMatchesList(matches.values().toArray());
        return new Response(true, response);
    }

    public Response getMatchProfile(int number) {
        if (matches == null) {
            return tryFillMatches();
        }
        if (number > matches.size() || number < 1) return new Response(false, "Нет такого номера!");
        var uuid = matches.keySet().toArray()[number - 1].toString();
        return new Response(true, showProfileById(uuid));
    }

    public Response changeProfileMessage(String message) {
        if (profile == null) return new Response(false, "Представьтесь, пожалуйста!");
        return requestResponseService.changeProfileMessage(profile.getSessionId(), message);
    }

    public String showProfileById(String nextProfileUUID) {
        var map = Objects.requireNonNull(requestResponseService.getPublicProfileInfo(nextProfileUUID));
        return viewGenerator.createProfileView(map.get("username"), map.get("sex"), map.get("profile_message"));
    }

    private Response getNextUUID() {
        if (profile == null) {
            var response = requestResponseService.getNextByOrderUUID(rowNumber);
            if (response.isStatus()) {
                rowNumber++;
                return response;
            }
            rowNumber = 0;
            return getNextUUID();
        }
        return requestResponseService.getNextRelatedUUID(profile.getSessionId());
    }


    private boolean setupCurrentProfileIsFail() {
        var response = getNextUUID();
        if (response.isStatus()) {
            String nextProfileUUID = response.getAttach().toString();
            profile.setCurrentProfile(nextProfileUUID);
            return false;
        }
        return true;
    }

    private String changeProfileMessage(String profileMessage, String s) {
        if (!profileMessage.equals("")) {
            var result = requestResponseService.changeProfileMessage(profile.getSessionId(), profileMessage);
            if (!result.isStatus()) s += result.getAttach().toString() + "\n";
        }
        return s;
    }

    private Response tryFillMatches() {
        var matchesResponse = showAll();
        if (matchesResponse.isStatus()) {
            return new Response(true,
                    "Обновляем список любимцев.\n\n" +
                            matchesResponse.getAttach().toString() +
                            "\nГотово! Попробуйте ещё!");
        } else {
            return new Response(false, matchesResponse.getAttach());
        }
    }

}
