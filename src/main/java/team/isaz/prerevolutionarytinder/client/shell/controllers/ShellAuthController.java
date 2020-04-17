package team.isaz.prerevolutionarytinder.client.shell.controllers;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;

@ShellComponent
@ShellCommandGroup("Управленіе входовъ и регистрацій")
public class ShellAuthController {

    CommandStatusService commandStatusService;
    RequestResponseService requestResponseService;

    public ShellAuthController(RequestResponseService requestResponseService, CommandStatusService commandStatusService) {
        this.requestResponseService = requestResponseService;
        this.commandStatusService = commandStatusService;
    }

    @ShellMethod(key = {"новая", "register"}, value = "Свайп влево")
    @ShellMethodAvailability("checkAvailability")
    public String register(@ShellOption String username,
                           @ShellOption String password,
                           @ShellOption String sex,
                           @ShellOption(defaultValue = "") String profileMessage) {

        var response = requestResponseService.register(username, password, sex, profileMessage);
        if (response.isStatus()) {
            commandStatusService.goMain();
        }
        return response.getAttach().toString();
    }

    @ShellMethod(key = {"войти", "login"}, value = "Свайп влево")
    @ShellMethodAvailability("checkAvailability")
    public String login(@ShellOption String username,
                        @ShellOption String password) {

        var response = requestResponseService.login(username, password);
        if (response.isStatus()) {
            commandStatusService.goMain();
        }
        return response.getAttach().toString();
    }

    public Availability checkAvailability() {
        return commandStatusService.isAuth()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Управленіи входовъ и регистрацій»");
    }
}
