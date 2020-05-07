package coursex;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class HomeworkComponentController {
    @FXML private VBox  _homeworkCompletedBox;
    @FXML private Label _courseNameLabel;
    @FXML private Label _homeworkNameLabel;
    @FXML private Label _homeworkDescriptionLabel;

    public void init(Homework homework, HomeworkSceneContext context) {
        this._courseNameLabel.setText(homework.courseName);
        this._courseNameLabel.setStyle(
            String.format(
                "-fx-padding: 1 9 1 9;" +
                "-fx-font-size: 15;" +
                "-fx-background-color: %s;" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: #424242;",
                context.courseColorAllocator.getColor(homework.courseName)
            )
        );
        this._homeworkNameLabel.setText(homework.name);
        this._homeworkCompletedBox.setVisible(homework.isCompleted);
        this._homeworkDescriptionLabel.setText(homework.description);
    }
}
