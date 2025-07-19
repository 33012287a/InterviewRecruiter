package com.learn.interviewrecruiter;

import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownRender {
    public static TextFlow renderMarkdown(String markdown) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(5);
        textFlow.getStyleClass().add("text-flow");

        String[] lines = markdown.split("\n");
        boolean inCodeBlock = false;
        boolean isJavaBlock = false;
        StringBuilder codeBlockBuilder = new StringBuilder();

        for (String line : lines) {
            if (line.strip().startsWith("```")) {
                if (!inCodeBlock) {
                    String lang = line.strip().toLowerCase();
                    isJavaBlock = lang.equals("```java");
                    inCodeBlock = true;
                } else {
                    inCodeBlock = false;
                    TextFlow codeRender;
                    if (isJavaBlock) {
                        codeRender = renderJavaCodeBlock(codeBlockBuilder.toString());
                    } else {
                        codeRender = renderPlainCodeBlock(codeBlockBuilder.toString());
                    }

                    codeRender.getStyleClass().add("code-block");
                    textFlow.getChildren().add(codeRender);
                    textFlow.getChildren().add(new Text("\n"));
                    codeBlockBuilder.setLength(0);
                }
                continue;
            }
            if (inCodeBlock) {
                codeBlockBuilder.append(line).append("\n");
            } else {
                if (line.startsWith("##")) {
                    Text header = new Text(line.substring(2) + "\n");
                    header.setFont(Font.font("System", FontWeight.BOLD, 16));
                    header.setFill(Color.DARKBLUE);
                    textFlow.getChildren().add(header);
                } else if (line.matches("(^\\s*[-*+]\\s+.*)")) {
                    String item = line.replaceFirst("(^\\s*[-*+]\\s+)", "• ");
                    List<Text> listText = parserInLineMarkdown(item);
                    for (Text text : listText) {
                        text.getStyleClass().add("list-item");
                    }
                    textFlow.getChildren().addAll(listText);
                    textFlow.getChildren().add(new Text("\n"));
                } else {
                    textFlow.getChildren().addAll(parserInLineMarkdown(line));
                    textFlow.getChildren().add(new Text("\n"));
                }
            }
        }
        return textFlow;
    }

    private static List<Text> parserInLineMarkdown(String text) {
        List<Text> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "(`[^`]+?`)|" +
                        "(\\*\\*[^*]+?\\*\\*)|" +
                        "(__[^_]+?__)|" +
                        "(\\*[^*]+?\\*)|" +
                        "(_[^_]+?_)|" +
                        "([^*_`]+)"
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String match = matcher.group();
            Text t;
            if (matcher.group(1) != null) {
                String code = matcher.group(1).substring(1, matcher.group(1).length() - 1);
                t = new Text(code);
                t.getStyleClass().add("inline-code");
            } else if (matcher.group(2) != null || matcher.group(3) != null) {
                String content = matcher.group().substring(2, match.length() - 2);
                t = new Text(content);
                t.getStyleClass().add("bold");
            } else if (matcher.group(4) != null || matcher.group(5) != null) {
                String content = matcher.group().substring(1, match.length() - 1);
                t = new Text(content);
                t.getStyleClass().add("italic");
            } else {
                t = new Text(matcher.group());
                t.getStyleClass().add("markdown-text");
            }
            result.add(t);
        }
        return result;
    }

    private static TextFlow renderJavaCodeBlock(String code) {
        TextFlow codeFlow = new TextFlow();
        codeFlow.setLineSpacing(2);

        String[] keywords = {
                "abstract", "assert", "boolean", "break",  "byte", "case", "catch", "char", "class", "const",  "continue",
                "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "if",
                "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
                "private",  "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
                "var", "true", "false"
        };

        Pattern pattern = Pattern.compile(
                "(//.*?$)|" +
                        "(/\\*.*?\\*/)|" +
                        "(\"(\\\\.|[^\"\\\\])*?\")|" +
                        "\\b(" + String.join("|", keywords) + ")\\b",
                Pattern.MULTILINE | Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(code);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                Text plain  = new Text(code.substring(lastEnd, matcher.start()));
                codeFlow.getChildren().add(plain);
            }

            String match = matcher.group();

            Text styled = new Text(match);
            if (match.startsWith("//") || match.startsWith("/*")) {
                styled.setFill(Color.GRAY);
            } else if (match.startsWith("\"")) {
                styled.setFill(Color.DARKGREEN);
            } else if (Arrays.asList(keywords).contains(match)) {
                styled.setFill(Color.DARKBLUE);
                styled.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
            } else {
                styled.setFill(Color.BLACK);
            }
            codeFlow.getChildren().add(styled);
            lastEnd = matcher.end();
        }

        if (lastEnd < code.length()) {
            Text tail =new Text(code.substring(lastEnd));
            codeFlow.getChildren().add(tail);
        }

        for (javafx.scene.Node node : codeFlow.getChildren()) {
            ((Text) node).setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
        }
        return codeFlow;
    }

    private static TextFlow renderPlainCodeBlock(String code) {
        TextFlow codeFlow = new TextFlow();
        Text text = new Text(code);
        text.setFont(Font.font("Consolas",16));
        text.setFill(Color.BLACK);
        codeFlow.getChildren().add(text);
        return codeFlow;
    }
}
