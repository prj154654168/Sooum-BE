package com.sooum.core.global.regex;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class BadWordFiltering implements BadWords {

    private final Set<String> badWordsSet = new HashSet<>(List.of(badWords));
    private final Map<String, Pattern> badWordPatterns = new HashMap<>();

    @PostConstruct // ①
    public void compileBadWordPatterns() {
        String patternText = buildPatternText();

        for (String word : badWordsSet) {
            String[] chars = word.split("");
            badWordPatterns.put(word, Pattern.compile(String.join(patternText, chars)));
        }
    }

    private String buildPatternText() {
        StringBuilder delimiterBuilder = new StringBuilder("[");
        for (String delimiter : delimiters) {
            delimiterBuilder.append(Pattern.quote(delimiter));
        }
        delimiterBuilder.append("]*");
        return delimiterBuilder.toString();
    }

    public boolean checkBadWord(String input) {
        for (Pattern pattern : badWordPatterns.values()) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        return false;
    }
}
