package team.isaz.prerevolutionarytinder.client.shell.services;

import lombok.Getter;

@Getter
public class CommandStatusService {
    private boolean main;
    private boolean match;
    private boolean auth;

    public CommandStatusService() {
        main = true;
        match = false;
        auth = false;
    }

    public void goMain() {
        main = true;
        match = false;
        auth = false;
    }

    public void goMatch() {
        main = false;
        match = true;
        auth = false;
    }

    public void goAuth() {
        main = false;
        match = false;
        auth = true;
    }
}
