package com.learn.interviewrecruiter;

import java.io.File;
import java.util.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import static com.learn.interviewrecruiter.MarkdownRender.renderMarkdown;

public class HelloController {
    private List<String> questionList;
    private List<String> answerList;
    private int currentIndex = 0;
    private OpenAiClient openAiClient = new OpenAiClient();
    private String fileAbsolutePath;
    private boolean isViewAnswerText = false;

    @FXML
    private ResourceBundle resources;
    @FXML
    private Label questionText;
    @FXML
    private TextArea userAnswer;
    @FXML
    private Button nextQuestionBtn;
    @FXML
    private Button previousQuestionBtn;
    @FXML
    private ScrollPane answerScrollPane;
    @FXML
    private TextFlow answerText;
    @FXML
    private Label answerOutput;
    @FXML
    private Button fileBtn;
    @FXML Button viewAnswerBtn;

    @FXML
    public void initialize() {
        fileBtn.setDisable(true);
        questionText.setText("Загрузка...");
        Platform.runLater(() -> {
            fileBtn.setDisable(false);
            questionText.setText("Выберите файл с вопросами");
        });
    }

    @FXML
    public void onFileChoose() {
        Platform.runLater(() -> {
            if (fileBtn.getScene() == null) {
                System.err.println("Сцена еще не инициализирована");
                return;
            }
        });

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с вопросами");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Markdown файлы", "*.md"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );

        File file = fileChooser.showOpenDialog(fileBtn.getScene().getWindow());

        if (file != null) {
            fileAbsolutePath = file.getAbsolutePath();
            loadQuestions();
        } else {
            questionText.setText("Файл не выбран");
        }
    }

    private void loadQuestions() {
        questionList = new LinkedList<>();
        answerList = new LinkedList<>();

        Map<String, String> faq = MarkdownParser.markdownParser(fileAbsolutePath);

        if (faq.isEmpty()) {
            questionText.setText("Фаил не содержит данных");
            return;
        } else {
            for (Map.Entry<String, String> entry : faq.entrySet()) {
                questionList.add(entry.getKey());
                answerList.add(entry.getValue());
            }
        }


        currentIndex = 0;
        showCurrentQuestion();
    }

    private void showCurrentQuestion() {
        if (currentIndex < questionList.size()) {
            questionText.setText(questionList.get(currentIndex));
            isViewAnswerText = false;
            toggleAnswerView();
            answerOutput.setText("");
        } else {
            questionText.setText("Вопросов больше нет");
            answerText.getChildren().clear();
        }
    }


    @FXML
    public void nextQuestionBtn() {
        if (questionList == null || questionList.isEmpty()) {
            questionText.setText("Вопросов нет");
            return;
        }
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showCurrentQuestion();
        }
    }

    @FXML
    public void previousQuestionBtn() {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentQuestion();
        } else {
            questionText.setText("Вопросов нет");
        }
    }

    @FXML
    public void answer() {
        if (questionList == null || questionList.isEmpty() || answerList == null) {
            questionText.setText("Вопросы не загружены");
            return;
        }

        if (currentIndex >= answerList.size()) {
            questionText.setText("Текущий индекс в не диапазона вопросов");
            return;
        }
        String userAns = userAnswer.getText().trim();
        if (userAns.isEmpty()) {
            questionText.setText("Введите ваш ответ.");
            return;
        }

        String question = questionList.get(currentIndex);
        String goldAnswer = answerList.get(currentIndex);

        openAiClient.getConnection(question, goldAnswer, userAns, feedback -> {
            Platform.runLater(() -> answerOutput.setText(feedback));
        });

        userAnswer.clear();
    }

    @FXML
    public void toggleAnswerView() {
        if (questionList == null || answerList == null || questionList.isEmpty()) {
            questionText.setText("Файл не выбран");
            return;
        }
        if (isViewAnswerText) {
            viewAnswerBtn.setText("Скрыть ответ");
            showMarkdownAnswer(answerList.get(currentIndex));
        } else {
            viewAnswerBtn.setText("Показать ответ");
            answerText.getChildren().clear();
        }
        isViewAnswerText = !isViewAnswerText;

    }
    public void showMarkdownAnswer(String mdContent) {
        answerText.getChildren().clear();
        TextFlow parsed = MarkdownRender.renderMarkdown(mdContent);
        answerText.getChildren().addAll(parsed.getChildren());
    }
}
