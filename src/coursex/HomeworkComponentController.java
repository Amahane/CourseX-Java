package coursex;

import java.time.*;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HomeworkComponentController {
    @FXML private VBox   completedBox;
    @FXML private VBox   notCompletedBox;
    @FXML private Label  dueLabel;
    @FXML private Label  nameLabel;
    @FXML private Label  scoreLabel;
    @FXML private Label  scoreTotalLabel;
    @FXML private Label  courseNameLabel;
    @FXML private Label  remainingDaysLabel;
    @FXML private Label  remainingDaysDescriptionLabel;
    @FXML private Region decorativeStripe;

    public void init(Homework homework, HomeworkSceneContext context) {
        var color = HomeworkComponentController.getColor(homework);

        this.initGenericText (homework);
        this.initGenericStyle(color, context.courseColorAllocator.getColor(homework.courseName));

        if (! homework.isCompleted)
        {
            this.initNotCompletedBoxText (homework);
            this.initNotCompletedBoxStyle(color);
        }
    }

    private void initGenericText(Homework homework) {
        this.dueLabel.setText(HomeworkComponentController.getDueString(homework.due));
        this.nameLabel.setText(homework.name);
        this.scoreLabel.setText(homework.score);
        this.scoreTotalLabel.setText(homework.scoreTotal);
        this.courseNameLabel.setText(homework.courseName);
    }

    private void initGenericStyle(String color, String courseColor) {
        this.dueLabel.setStyle(
            "-fx-font-family: Ubuntu;" +
            "-fx-font-size: 18;" +
            "-fx-text-fill: " + color
        );
        this.courseNameLabel.setStyle(
            "-fx-padding: 1 9 1 9;" +
            "-fx-font-size: 15;" +
            "-fx-background-color: " + courseColor + ";" +
            "-fx-background-radius: 12;" +
            "-fx-text-fill: #424242;"
        );
        this.decorativeStripe.setStyle("-fx-background-color: " + color);
    }

    private void initNotCompletedBoxText(Homework homework) {
        this.remainingDaysLabel.setText(
            HomeworkComponentController.getRemainingDaysString(homework.due)
        );
        this.remainingDaysDescriptionLabel.setText(
            homework.due.isBefore(LocalDateTime.now())
                ? "Days Later"
                : "Days Ago");
    }

    private void initNotCompletedBoxStyle(String color) {
        this.completedBox.setVisible(false);
        this.notCompletedBox.setVisible(true);
        this.remainingDaysLabel.setStyle(
            "-fx-font-size: 48;" +
                "-fx-text-fill: " + color
        );
        this.remainingDaysDescriptionLabel.setStyle(
            "-fx-font-size: 12;" +
                "-fx-text-fill: " + color
        );
    }

    private static String getColor(Homework homework) {
        return homework.isCompleted
            ? "#4caf50"
            : homework.due.isAfter(LocalDateTime.now())
                ? "#e91e63"
                : "#ff9800";
    }

    private static String getDueString(LocalDateTime due) {
        return String.format(
            "Due: %04d.%02d.%02d  %02d:%02d",
            due.getYear(),
            due.getMonth().getValue() + 1,
            due.getDayOfMonth(),
            due.getHour(),
            due.getMinute()
        );
    }

    private static String getRemainingDaysString(LocalDateTime due) {
        return String.format(
            "%02d",
            Math.abs(Duration.between(due, LocalDateTime.now()).toDays())
        );
    }
}
