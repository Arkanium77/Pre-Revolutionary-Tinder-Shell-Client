package team.isaz.prerevolutionarytinder.client.shell.domain;

import lombok.Data;
import team.isaz.prerevolutionarytinder.client.shell.services.helper.StringConvertationUtils;

@Data
public class ClientProfile {
    private String sessionId;
    private String username;
    private String password;
    private String currentProfile;

    public ClientProfile(String sessionId, String username, String password, String currentProfile) {
        this.sessionId = sessionId;
        this.username = username;
        this.password = password;
        this.currentProfile = currentProfile;
    }

    public ClientProfile(String sessionId, String username, String password) {
        this.sessionId = sessionId;
        this.username = username;
        this.password = password;
    }

    public void setCurrentProfile(String currentProfile) {
        if (StringConvertationUtils.isThatUUID(currentProfile)) {
            this.currentProfile = currentProfile;
        } else {
            throw new RuntimeException("Invalid currentProfile string");
        }
    }
}
