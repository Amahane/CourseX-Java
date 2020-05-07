package coursex;

import java.time.*;
import java.util.*;

import javafx.fxml.*;
import javafx.collections.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

import com.jfoenix.controls.*;

public class HomeworkComponentController {
    @FXML private VBox   completedBox;
    @FXML private VBox   notCompletedBox;
    @FXML private Label  dueLabel;
    @FXML private Label  nameLabel;
    @FXML private Label  scoreLabel;
    @FXML private Label  scoreTotalLabel;
    @FXML private Label  remainingDaysLabel;
    @FXML private Label  remainingDaysDescriptionLabel;
    @FXML private Region decorativeStripe;
    @FXML private JFXListView<String> tagListView;

    public void init(Homework homework, HomeworkSceneContext context) {
        var color = HomeworkComponentController.getColor(homework);

        this.initGenericText (homework);
        this.initGenericStyle(color);
        this.initTagListView (homework, context);

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
    }

    private void initGenericStyle(String color) {
        this.dueLabel.setStyle(
            "-fx-font-family: Ubuntu;" +
            "-fx-font-size: 18;" +
            "-fx-text-fill: " + color
        );
        this.decorativeStripe.setStyle("-fx-background-color: " + color);
    }

    private void initNotCompletedBoxText(Homework homework) {
        this.remainingDaysLabel.setText(
            HomeworkComponentController.getRemainingDaysString(homework.due)
        );
        this.remainingDaysDescriptionLabel.setText(
            homework.due.isBefore(LocalDateTime.now())
                ? "Days Ago"
                : "Days Later");
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

    private void initTagListView(
        Homework             homework,
        HomeworkSceneContext context
    ) {
        var tagsRendered = new ArrayList<>(homework.tags);
        tagsRendered.add(0, homework.courseName);
        this.tagListView.getStylesheets().add(
            this.getClass().getResource("hideScrollbar.css").toExternalForm()
        );
        this.tagListView.setItems(FXCollections.observableList(tagsRendered));
        this.tagListView.setCellFactory(listView -> new JFXListCell<>() {
                @Override protected void updateItem(String tag, boolean empty) {
                this.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-insets: 0; -fx-padding: 0;"
                );
                if (empty) return;
                var label = new Label();
                label.setText(tag);
                label.setStyle(
                    "-fx-padding: 1 9 1 9;" +
                    "-fx-font-size: 15;" +
                    "-fx-text-fill: #424242;" +
                    "-fx-background-color: " +
                    context.courseColorAllocator.getColor(tag) + ";" +
                    "-fx-background-radius: 12;"
                );
                var container = new VBox();
                container.setStyle("-fx-padding: 0 6 0 0");
                container.getChildren().add(label);
                this.setGraphic(container);
                }
            }
        );
    }

    private static String getColor(Homework homework) {
        return homework.isCompleted
            ? "#4caf50"
            : homework.due.isBefore(LocalDateTime.now())
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
