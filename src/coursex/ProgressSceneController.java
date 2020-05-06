package coursex;

import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.util.*;

public class ProgressSceneController implements Initializable {
    @FXML private JFXProgressBar _progressBar;
    @FXML private Label   _iaaaLoginLabel;
    @FXML private SVGPath _iaaaLoginIcon;
    @FXML private Label   _courseLoginLabel;
    @FXML private SVGPath _courseLoginIcon;
    @FXML private Label   _parseCourseListLabel;
    @FXML private SVGPath _parseCourseListIcon;
    @FXML private Label   _parseCourseLabel;
    @FXML private SVGPath _parseCourseIcon;

    @Override @FXML public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initTaskMaps();
    }

    public void onReset() {
        this._progressBar.setProgress(0);

        this.markProcessing(TaskName.IAAA_LOGIN);
        this.markWaiting(TaskName.COURSE_LOGIN);
        this.markWaiting(TaskName.PARSE_COURSE_LIST);
        this.markWaiting(TaskName.PARSE_COURSE);
    }

    public void onCompleteIaaaLogin() {
        this._progressBar.setProgress(0.15);
        this.markDone(TaskName.IAAA_LOGIN);
        this.markProcessing(TaskName.COURSE_LOGIN);
    }

    public void onCompleteCourseLogin() {
        this._progressBar.setProgress(0.3);
        this.markDone(TaskName.COURSE_LOGIN);
        this.markProcessing(TaskName.PARSE_COURSE_LIST);
    }

    public void onCompleteParseCourseList(Map<String, String> courseNames) {
        this._progressBar.setProgress(0.4);
        this.markDone(TaskName.PARSE_COURSE_LIST);
        this.markProcessing(TaskName.PARSE_COURSE);
        this._totalCourseCount  = courseNames.size();
        this._parsedCourseCount = 0;
        this._parseCourseLabel.setText(this.buildParseCourseLabelText());
    }

    public void onCompleteParseCourse(int courseID) {
        ++ this._parsedCourseCount;
        this._progressBar.setProgress(
            0.4 +
            0.6 * this._parsedCourseCount / this._totalCourseCount);
        this._parseCourseLabel.setText(this.buildParseCourseLabelText());
    }

    private enum TaskName {
        IAAA_LOGIN,
        COURSE_LOGIN,
        PARSE_COURSE_LIST,
        PARSE_COURSE
    }

    private Map<TaskName, SVGPath> _taskIconMap;
    private Map<TaskName, Label>   _taskLabelMap;

    private static final String COLOR_DONE          = "#2e7d32";
    private static final String COLOR_WAITING       = "#9e9e9e";
    private static final String COLOR_PROCESSING    = "#1e88e5";
    private static final String SVG_ICON_DONE       = "M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z";
    private static final String SVG_ICON_PROCESSING = "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z";

    private int _totalCourseCount  = -1;
    private int _parsedCourseCount = -1;

    private String buildParseCourseLabelText() {
        return String.format(
            "解析课程信息 (%d/%d)",
            this._parsedCourseCount,
            this._totalCourseCount
        );
    }

    private void initTaskMaps() {
        this._taskIconMap = new HashMap<>();
        this._taskIconMap .put(TaskName.IAAA_LOGIN       , this._iaaaLoginIcon);
        this._taskIconMap .put(TaskName.COURSE_LOGIN     , this._courseLoginIcon);
        this._taskIconMap .put(TaskName.PARSE_COURSE_LIST, this._parseCourseListIcon);
        this._taskIconMap .put(TaskName.PARSE_COURSE     , this._parseCourseIcon);

        this._taskLabelMap = new HashMap<>();
        this._taskLabelMap.put(TaskName.IAAA_LOGIN       , this._iaaaLoginLabel);
        this._taskLabelMap.put(TaskName.COURSE_LOGIN     , this._courseLoginLabel);
        this._taskLabelMap.put(TaskName.PARSE_COURSE_LIST, this._parseCourseListLabel);
        this._taskLabelMap.put(TaskName.PARSE_COURSE     , this._parseCourseLabel);
    }

    private void markWaiting(TaskName taskName) {
        var icon  = this._taskIconMap .get(taskName);
        var label = this._taskLabelMap.get(taskName);
        icon .setContent(SVG_ICON_DONE);
        icon .setVisible(false);
        icon .setFill   (Paint.valueOf(COLOR_WAITING));
        label.setStyle  (String.format("-fx-text-fill: %s;", COLOR_WAITING));
    }

    private void markProcessing(TaskName taskName) {
        var icon  = this._taskIconMap .get(taskName);
        var label = this._taskLabelMap.get(taskName);
        icon .setContent(SVG_ICON_PROCESSING);
        icon .setVisible(true);
        icon .setFill   (Paint.valueOf(COLOR_PROCESSING));
        label.setStyle  (String.format("-fx-text-fill: %s;", COLOR_PROCESSING));
    }

    private void markDone(TaskName taskName) {
        var icon  = this._taskIconMap .get(taskName);
        var label = this._taskLabelMap.get(taskName);
        icon .setContent(SVG_ICON_DONE);
        icon .setFill   (Paint.valueOf(COLOR_DONE));
        label.setStyle  (String.format("-fx-text-fill: %s;", COLOR_DONE));
    }
}
