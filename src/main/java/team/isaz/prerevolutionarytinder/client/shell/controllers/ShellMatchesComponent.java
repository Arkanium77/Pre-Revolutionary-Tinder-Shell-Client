package team.isaz.prerevolutionarytinder.client.shell.controllers;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;

@ShellComponent
@ShellCommandGroup("Управленіе Любимцевъ")
public class ShellMatchesComponent {

    CommandStatusService commandStatusService;
    RequestResponseService requestResponseService;

    public ShellMatchesComponent(RequestResponseService requestResponseService, CommandStatusService commandStatusService) {
        this.requestResponseService = requestResponseService;
        this.commandStatusService = commandStatusService;
    }

    @ShellMethod(key = {"весь списокъ", "showAll"}, value = "Показать все анкеты")
    @ShellMethodAvailability("checkAvailability")
    public String showAll() {
        return requestResponseService.showAll();
    }

    @ShellMethod(key = {"покажи", "show"}, value = "Показать анкету номер...")
    @ShellMethodAvailability("checkAvailability")
    public String show(@ShellOption int number) {
        return requestResponseService.getMatchProfile(number);
    }

    public Availability checkAvailability() {
        return commandStatusService.isMatch()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Управленіи Любимцевъ»");
    }
}
