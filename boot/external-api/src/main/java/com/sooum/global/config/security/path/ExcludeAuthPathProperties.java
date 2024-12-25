package com.sooum.global.config.security.path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@AllArgsConstructor
@ConfigurationProperties("exclude-auth-path-patterns")
public class ExcludeAuthPathProperties {
    private List<AuthPath> paths;

    public List<String> getExcludeAuthPaths() {
        return paths.stream().map(AuthPath::getPathPattern).toList();
    }

    @Getter
    @AllArgsConstructor
    public static class AuthPath {
        private String pathPattern;
        private String method;
}
}
