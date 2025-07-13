package com.learn.interviewrecruiter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    public static Map<String, String> markdownParser(String file) {
        Map<String, String> questions = new LinkedHashMap<>();
        try {
            if (!Files.exists(Paths.get(file))) {
                System.err.println("Фиал не найден " + file);
                return questions;
            }
            String content = Files.readString(Paths.get(file));

            Pattern pattern = Pattern.compile("(?m)^##\\s*(.+?)\\s*\\n(.*?)(?=^##\\s|\\z)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String question = matcher.group(1).trim();
                String answer = matcher.group(2).trim();
                answer = answer.replaceAll("(?m)^\\[.*?\\]\\(.*?\\)\\s*$", "");
                answer = answer.replaceAll("(?s)# Источники.*", "");
                answer = answer.replaceAll("(?m)^#+\\s*", "");
                questions.put(question, answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
