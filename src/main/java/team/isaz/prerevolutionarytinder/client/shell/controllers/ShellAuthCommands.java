package team.isaz.prerevolutionarytinder.client.shell.controllers;

import lombok.var;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandHandlerService;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;

@ShellComponent
@ShellCommandGroup("Управленіе входовъ и регистрацій")
public class ShellAuthCommands {

    private final CommandStatusService commandStatusService;
    private final CommandHandlerService commandHandlerService;

    public ShellAuthCommands(CommandHandlerService commandHandlerService, CommandStatusService commandStatusService) {
        this.commandHandlerService = commandHandlerService;
        this.commandStatusService = commandStatusService;
    }

    @ShellMethod(key = {"новая", "register"}, value = "Создать новую анкету")
    @ShellMethodAvailability("checkAvailability")
    public String register(@ShellOption(defaultValue = "") String username,
                           @ShellOption(defaultValue = "") String password,
                           @ShellOption(defaultValue = "") String sex,
                           @ShellOption(defaultValue = "") String profileMessage) {
        if (username.equals("") || password.equals("") || sex.equals("")) return register();
        var response = commandHandlerService.register(username, password, sex, profileMessage);
        if (response.isStatus()) {
            commandStatusService.profileView();
        }
        return response.getAttach().toString();
    }

    public String register() {
        return "Дабы завершить создание анкеты введите «новая» и ответьте на вопросы: " +
                "Как вас величать? Ваш секретный шифръ? Вы сударь иль сударыня?";
    }


    @ShellMethod(key = {"войти", "login"}, value = "Войти в уже созданную анкету.")
    @ShellMethodAvailability("checkAvailability")
    public String login(@ShellOption(defaultValue = "") String username,
                        @ShellOption(defaultValue = "") String password) {
        if (username.equals("") || password.equals("")) return register();
        var response = commandHandlerService.login(username, password);
        if (response.isStatus()) {
            commandStatusService.profileView();
        }
        return response.getAttach().toString();
    }

    @ShellMethod(key = {"изменить сообщение", "change message"}, value = "Изменить сообщенiе анкеты.")
    @ShellMethodAvailability("checkAvailability")
    public String changeMessage(@ShellOption(defaultValue = "") String message) {
        var response = commandHandlerService.changeProfileMessage(message);
        return response.getAttach().toString();
    }

    public String login() {
        return "Сударь иль сударыня дабы войти введiте  логинъ  и пароль черезъ пробѣлъ!";
    }

    public Availability checkAvailability() {
        return commandStatusService.isAuth()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Управленіи входовъ и регистрацій»");
    }
}
