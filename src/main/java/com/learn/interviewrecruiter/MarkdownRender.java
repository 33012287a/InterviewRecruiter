package com.learn.interviewrecruiter;

import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownRender {
    public static TextFlow renderMarkdown(String markdown) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(5);

        String[] lines = markdown.split("\n");
        for (String line : lines) {
            if (line.startsWith("##")) {
                Text header = new Text(line.substring(3) + "\n");
                header.setFont(Font.font("System", FontWeight.BOLD, 16));
                header.setFill(Color.DARKBLUE);
                textFlow.getChildren().add(header);
            } else {
                textFlow.getChildren().addAll(parserInLineMArkdown(line));
                textFlow.getChildren().add(new Text("\n"));
            }
        }
        return textFlow;
    }

    private static Text[] parserInLineMArkdown(String text) {
        Pattern pattern = Pattern.compile("(\\*\\*(.+?)\\*\\*|__(.+?)__|\\*(.+?)\\*|_(.+?)_|[^*_]+)");
        Matcher matcher = pattern.matcher(text);

        TextFlow tempFlow = new TextFlow();
        while (matcher.find()) {
            String match = matcher.group();
            Text t;

            if (match.matches("\\*\\*(.+)\\*\\*") || match.matches("__(.+)__")) {
                String content = match.substring(2, match.length() - 2);
                t = new Text(content);
                t.setFont(Font.font("System", FontWeight.BOLD, 12));
            } else if (match.matches("\\*(.+)\\*") || match.matches("_(.+)_")) {
                String content = match.substring(1, match.length() - 1);
                t = new Text(content);
                t.setFont(Font.font("System", FontPosture.ITALIC, 12));
            } else {
                t = new Text(match);
                t.setFont(Font.font("System", 12));
            }
            tempFlow.getChildren().add(t);
        }
        return tempFlow.getChildren().toArray(new Text[0]);
    }
}
