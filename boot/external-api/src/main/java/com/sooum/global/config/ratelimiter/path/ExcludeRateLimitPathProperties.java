package com.sooum.global.config.ratelimiter.path;

import com.sooum.global.config.security.path.ExcludeAuthPathProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@AllArgsConstructor
@ConfigurationProperties("exclude-ratelimiter-path-patterns")
public class ExcludeRateLimitPathProperties {
    private List<ExcludeAuthPathProperties.AuthPath> paths;

    public List<String> getExcludeAuthPaths() {
        return paths.stream().map(ExcludeAuthPathProperties.AuthPath::getPathPattern).toList();
    }

    @Getter
    @AllArgsConstructor
    public static class AuthPath {
        private String pathPattern;
        private String method;
    }
}
