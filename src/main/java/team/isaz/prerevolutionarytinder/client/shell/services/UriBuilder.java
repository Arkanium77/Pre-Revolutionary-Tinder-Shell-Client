package team.isaz.prerevolutionarytinder.client.shell.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriBuilder {
    Logger logger = LoggerFactory.getLogger(UriBuilder.class);
    private String pathSplitter;
    private List<String> fragments;
    private String host;
    private Map<String, String> params;

    public UriBuilder() {
        host = "";
        pathSplitter = "/";
        fragments = new ArrayList<>();
        params = new HashMap<>();
    }

    public UriBuilder(String host) {
        this.host = host;
        pathSplitter = "/";
        fragments = new ArrayList<>();
        params = new HashMap<>();
    }

    public UriBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public UriBuilder withPathSplitter(String pathSplitter) {
        this.pathSplitter = pathSplitter;
        return this;
    }

    public UriBuilder withPathFragment(String fragment) {
        this.fragments.add(fragment);
        return this;
    }

    public UriBuilder withParam(String name, Object value) {
        this.params.put(name, value.toString());
        return this;
    }

    public URI build() {
        StringBuilder builder = new StringBuilder(host);
        fragments.forEach(s -> builder.append(pathSplitter).append(s));
        if (params.size() > 0) {
            builder.append("?");
            params.forEach((k, v) -> builder.append(k).append("=").append(v).append("&"));
            builder.deleteCharAt(builder.lastIndexOf("&"));
        }
        try {
            return new URI(builder.toString());
        } catch (URISyntaxException e) {
            logger.debug("URI Builder error: {}", e.getMessage());
        }
        return null;
    }
}
