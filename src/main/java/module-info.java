module com.learn.interviewrecruiter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires okhttp3;
    requires annotations;
    requires org.json;
    requires io.github.cdimascio.dotenv.java;


    opens com.learn.interviewrecruiter to javafx.fxml;
    exports com.learn.interviewrecruiter;
}