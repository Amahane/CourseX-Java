package coursex;

import java.net.*;
import java.util.*;

import javafx.fxml.*;
import javafx.collections.*;
import javafx.scene.layout.BorderPane;

import com.jfoenix.controls.*;

public class HomeworkSceneController implements Initializable {
    public final ObservableList<Homework> homeworkList;

    public HomeworkSceneController() {
        this.homeworkList = FXCollections.observableList(Mock.homeworks);
        this._context = new HomeworkSceneContext();
    }

    @Override @FXML public void initialize(URL url, ResourceBundle resourceBundle) {
        this._homeworkListView.setItems(this.homeworkList);
        this._homeworkListView.setCellFactory(listView -> new JFXListCell<>() {
            @Override protected void updateItem(Homework homework, boolean empty) {
                this.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-insets: 0; -fx-padding: 0;"
                );
                if (empty) return;
                var loader = new FXMLLoader(this.getClass().getResource("homeworkComponent.fxml"));
                try {
                    loader.load();
                    loader.<HomeworkComponentController>getController().init(
                        homework,
                        HomeworkSceneController.this._context);
                    this.setGraphic(loader.<BorderPane>getRoot());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML private JFXListView<Homework> _homeworkListView;

    private HomeworkSceneContext        _context;
}
