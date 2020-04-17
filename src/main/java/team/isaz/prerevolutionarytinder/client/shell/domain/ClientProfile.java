package team.isaz.prerevolutionarytinder.client.shell.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientProfile {
    UUID sessionId;
    String username;
    String password;
    boolean sex;
    UUID currentProfile;

    public ClientProfile(UUID sessionId, String username, String password, boolean sex, UUID currentProfile) {
        this.sessionId = sessionId;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.currentProfile = currentProfile;
    }

    public ClientProfile(UUID sessionId, String username, String password, boolean sex) {
        this.sessionId = sessionId;
        this.username = username;
        this.password = password;
        this.sex = sex;
    }
}
