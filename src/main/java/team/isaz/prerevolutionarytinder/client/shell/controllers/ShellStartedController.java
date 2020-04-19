package team.isaz.prerevolutionarytinder.client.shell.controllers;

import lombok.var;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandHandlerService;
import team.isaz.prerevolutionarytinder.client.shell.services.CommandStatusService;

@ShellComponent
@ShellCommandGroup("Главное управленіе")
public class ShellStartedController {
    CommandStatusService commandStatusService;
    CommandHandlerService commandHandlerService;

    public ShellStartedController(CommandHandlerService commandHandlerService, CommandStatusService commandStatusService) {
        this.commandHandlerService = commandHandlerService;
        this.commandStatusService = commandStatusService;
        init();
    }

    public void init() {
        System.out.println("Приветствую, неизвестного! " +
                "Дабы в полной мере насладиться обслуживанiем " +
                "создайте или войдите в свою анкету!\n\n");
        var response = commandHandlerService.showNext();
        if (!response.isStatus()) {
            System.out.println("Произошла непредвиденная ошибка! Перезапустите приложение!");
        } else {
            System.out.println(response.getAttach().toString() + "\n");
        }
    }

    @ShellMethod(key = {"вправо", "right"}, value = "Смахнуть вправо, дабы проявiть любовь.")
    @ShellMethodAvailability("checkAvailability")
    public String right() {
        var likeResponse = commandHandlerService.like();
        if (!likeResponse.isStatus()) return likeResponse.getAttach().toString();
        return likeResponse.getAttach().toString() +
                "\n\n" +
                commandHandlerService.showNext().getAttach().toString() + "\n";
    }


    @ShellMethod(key = {"влево", "left"}, value = "Смахнуть влево, дабы неодобрiть.")
    @ShellMethodAvailability("checkAvailability")
    public String left() {
        var dislikeResponse = commandHandlerService.dislike();
        if (!dislikeResponse.isStatus()) return dislikeResponse.getAttach().toString();
        return dislikeResponse.getAttach().toString() +
                "\n\n" +
                commandHandlerService.showNext().getAttach().toString() + "\n";
    }

    @ShellMethod(key = {"анкета", "profile"}, value = "Перейти в «Управленіе входовъ и регистрацій»")
    @ShellMethodAvailability("checkAvailability")
    public String profile() {
        commandStatusService.goAuth();
        return "\nПерешли в «Управленіи входовъ и регистрацій»\n";
    }

    @ShellMethod(key = {"любимцы", "matches"}, value = "Перейти в «Управленіе Любимцевъ»")
    @ShellMethodAvailability("checkAvailability")
    public String matches() {
        commandStatusService.goMatch();
        return "\nПерешли в «Управленіе Любимцевъ»\n\n" + commandHandlerService.showAll().getAttach().toString() + "\n";
    }

    @ShellMethod(key = {"уйти", "leave"}, value = "Вернуться в «Главное Управленіе»")
    @ShellMethodAvailability("checkNotInMain")
    public String back() {
        commandStatusService.goMain();
        return "\nВернулись въ «Главное Управленіе»\n";
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
