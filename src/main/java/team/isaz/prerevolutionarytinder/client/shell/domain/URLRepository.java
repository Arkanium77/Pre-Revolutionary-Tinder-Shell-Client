package team.isaz.prerevolutionarytinder.client.shell.domain;

import team.isaz.prerevolutionarytinder.client.shell.utils.UriBuilder;

import java.net.URI;

public class URLRepository {
    private final String host;

    public URLRepository(String host) {
        this.host = host;
    }

    public URI getNextUserByRowNumber(int rowNumber) {
        return new UriBuilder(host)
                .withPathFragment("public_info")
                .withPathFragment("next_user")
                .withParam("row_number", rowNumber)
                .build();
    }

    public URI sendRelation() {
        return new UriBuilder(host)
                .withPathFragment("relations")
                .withPathFragment("send_relation")
                .build();
    }

    public URI getAllMatches() {
        return new UriBuilder(host)
                .withPathFragment("relations")
                .withPathFragment("get_all_matches")
                .build();
    }

    public URI getRelatedUUID() {
        return new UriBuilder(host)
                .withPathFragment("relations")
                .withPathFragment("get_next")
                .build();
    }

    public URI getPublicProfileInfo(String uuid) {
        return new UriBuilder(host)
                .withPathFragment("public_info")
                .withPathFragment("profile_info")
                .withParam("uuid", uuid)
                .build();
    }

    public URI changeProfileMessage() {
        return new UriBuilder(host)
                .withPathFragment("public_info")
                .withPathFragment("setup_profile_message")
                .build();
    }

    public URI register() {
        return new UriBuilder(host)
                .withPathFragment("authorization")
                .withPathFragment("register")
                .build();
    }

    public URI login() {
        return new UriBuilder(host)
                .withPathFragment("authorization")
                .withPathFragment("login")
                .build();
    }
}
