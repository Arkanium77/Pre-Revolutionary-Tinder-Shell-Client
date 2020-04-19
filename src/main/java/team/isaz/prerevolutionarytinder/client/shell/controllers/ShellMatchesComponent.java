package team.isaz.prerevolutionarytinder.client.shell.controllers;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandHandlerService;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;

@ShellComponent
@ShellCommandGroup("Управленіе Любимцевъ")
public class ShellMatchesComponent {

    CommandStatusService commandStatusService;
    CommandHandlerService commandHandlerService;

    public ShellMatchesComponent(CommandHandlerService commandHandlerService, CommandStatusService commandStatusService) {
        this.commandHandlerService = commandHandlerService;
        this.commandStatusService = commandStatusService;
    }

    @ShellMethod(key = {"весь списокъ", "showAll"}, value = "Показать все анкеты")
    @ShellMethodAvailability("checkAvailability")
    public String showAll() {
        return commandHandlerService.showAll().getAttach().toString() + "\n";
    }

    @ShellMethod(key = {"покажи", "show"}, value = "Показать анкету номер...")
    @ShellMethodAvailability("checkAvailability")
    public String show(@ShellOption int number) {
        return commandHandlerService.getMatchProfile(number).getAttach().toString() + "\n";
    }

    public Availability checkAvailability() {
        return commandStatusService.isMatch()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Управленіи Любимцевъ»");
    }
}
