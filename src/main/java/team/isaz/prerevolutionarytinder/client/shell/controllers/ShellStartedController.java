package team.isaz.prerevolutionarytinder.client.shell.controllers;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;

@ShellComponent
@ShellCommandGroup("Главное управленіе")
public class ShellStartedController {
    CommandStatusService commandStatusService;
    RequestResponseService requestResponseService;

    public ShellStartedController(RequestResponseService requestResponseService, CommandStatusService commandStatusService) {
        this.requestResponseService = requestResponseService;
        this.commandStatusService = commandStatusService;
    }

    @ShellMethod(key = {"влево", "left"}, value = "Свайп влево")
    @ShellMethodAvailability("checkAvailability")
    public String left() {
        return requestResponseService.like() +
                '\n' +
                requestResponseService.showNext();
    }

    @ShellMethod(key = {"вправо", "right"}, value = "Свайп вправо")
    @ShellMethodAvailability("checkAvailability")
    public String right() {
        return requestResponseService.dislike() +
                '\n' +
                requestResponseService.showNext();
    }

    @ShellMethod(key = {"анкета", "profile"}, value = "Логин/регистрация")
    @ShellMethodAvailability("checkAvailability")
    public String profile() {
        commandStatusService.goAuth();
        return "Перешли в «Управленіи входовъ и регистрацій»";
    }

    @ShellMethod(key = {"любимцы", "matches"}, value = "Совпадения? Не думаю")
    @ShellMethodAvailability("checkAvailability")
    public String matches() {
        commandStatusService.goMatch();
        return "Перешли в «Управленіе Любимцевъ»";
    }

    @ShellMethod(key = {"назад", "back"}, value = "Вернуться в «Главное Управленіе»")
    @ShellMethodAvailability("checkNotInMain")
    public String back() {
        return "\nВернулись въ " + commandStatusService.goMain() + '\n';
    }

    public Availability checkAvailability() {
        return commandStatusService.isMain()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Главномъ управленіи»");
    }

    public Availability checkNotInMain() {
        return !commandStatusService.isMain()
                ? Availability.available()
                : Availability.unavailable("вы уже находитесь въ «Главномъ управленіи»");
    }

}
