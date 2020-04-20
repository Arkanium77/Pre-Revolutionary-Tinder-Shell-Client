package team.isaz.prerevolutionarytinder.client.shell.controllers;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ShellProfileViewCommands {
    private final CommandStatusService commandStatusService;
    private final CommandHandlerService commandHandlerService;

    public ShellProfileViewCommands(CommandHandlerService commandHandlerService, CommandStatusService commandStatusService) {
        this.commandHandlerService = commandHandlerService;
        this.commandStatusService = commandStatusService;
        init();
    }

    public void init() {
        log.info("Приветствую, неизвестного! " +
                "Дабы в полной мере насладиться обслуживанiем " +
                "создайте или войдите в свою анкету!\n\n");
        var response = commandHandlerService.showNext();
        if (!response.isStatus()) {
            log.info("Произошла непредвиденная ошибка! Перезапустите приложение!");
        } else {
            log.info(response.getAttach().toString() + "\n");
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
        commandStatusService.auth();
        return "\nПерешли в «Управленіи входовъ и регистрацій»\n";
    }

    @ShellMethod(key = {"любимцы", "matches"}, value = "Перейти в «Управленіе Любимцевъ»")
    @ShellMethodAvailability("checkAvailability")
    public String matches() {
        commandStatusService.match();
        return "\nПерешли в «Управленіе Любимцевъ»\n\n" + commandHandlerService.showAll().getAttach().toString() + "\n";
    }

    @ShellMethod(key = {"уйти", "leave"}, value = "Вернуться в «Главное Управленіе»")
    @ShellMethodAvailability("checkNotInMain")
    public String back() {
        commandStatusService.profileView();
        return "\nВернулись въ «Главное Управленіе»\n" + commandHandlerService.showNext().getAttach().toString();
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
