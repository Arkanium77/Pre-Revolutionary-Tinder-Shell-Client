package team.isaz.prerevolutionarytinder.client.shell.controllers;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import team.isaz.prerevolutionarytinder.client.shell.services.RequestResponseService;

@ShellComponent
public class ShellStartedController {
    RequestResponseService service;

    public ShellStartedController(RequestResponseService service) {
        this.service = service;
    }

//    @ShellMethod("HelloWorld")
//    public String hi(@ShellOption(defaultValue = "Mark") String text) {
//        // invoke service
//        return "O, hi " + text;
//    }
    @ShellMethod(key = {"влево","left"}, value = "Свайп влево")
    public String left(){
        return "Левее";
    }
}
