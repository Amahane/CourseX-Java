package coursex;

import java.io.Console;
import java.util.*;
import javafx.fxml.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.application.*;
import org.apache.hc.core5.concurrent.*;
import org.apache.hc.client5.http.cookie.*;


public class Application extends javafx.application.Application {
    @Override public void start(Stage stage) throws Exception {
        Application._instance = this;

        this.loadFonts();
        this.loadScenes();

        this._mainStage = stage;
        stage.setTitle("CourseX");
        stage.setScene(this._loginScene);
        stage.show();
    }

    public void onLogin(String userName, String password) {
        this._progressSceneController.onReset();
        this._mainStage.setScene(this._progressScene);
        IaaaLogin.loginAsync(userName, password, new FutureCallback<>() {
            @Override public void completed(String token) {
                Application.this._progressSceneController.onCompleteIaaaLogin();
                CourseLogin.loginAsync(token, new FutureCallback<>() {
                    @Override public void completed(CookieStore cookieStore) {
                        Application.this._progressSceneController.onCompleteCourseLogin();
                        CourseListParser.fetchAndParse(cookieStore, new FutureCallback<>() {
                            @Override public void completed(
                                Map<String, String> courseNames
                            ) {
                                Platform.runLater(() -> Application.this
                                    ._progressSceneController
                                    .onCompleteParseCourseList(courseNames));
                                var multipleSession = new MultipleCourseParsingSession(
                                    cookieStore,
                                    courseNames.keySet(),
                                    new FutureCallback<>() {
                                        @Override public void completed(List<Homework> homeworks) {
                                            for (var e : homeworks)
                                                System.out.println(e.name);
                                        }
                                        @Override public void failed(Exception e) {
                                            Application.this.onApplicationException(
                                                e instanceof ApplicationException
                                                    ? (ApplicationException) e
                                                    : new ApplicationException("发生内部错误，数据获取失败，请尝试重新登录。", e));
                                        }
                                        @Override public void cancelled() {
                                            Application.this.onApplicationException(
                                                new ApplicationException("操作已取消。")
                                            );
                                        }
                                    }
                                );
                                multipleSession.start();
                            }
                            @Override public void failed(Exception e) {
                                Application.this.onApplicationException(
                                    e instanceof ApplicationException
                                        ? (ApplicationException) e
                                        : new ApplicationException("发生内部错误，登录失败，请尝试重新登录。", e));
                            }
                            @Override public void cancelled() {
                                Application.this.onApplicationException(
                                    new ApplicationException("登录已取消。")
                                );
                            }
                        });
                    }
                    @Override public void failed(Exception e) {
                        Application.this.onApplicationException(
                            e instanceof ApplicationException
                                ? (ApplicationException) e
                                : new ApplicationException("发生内部错误，登录失败，请尝试重新登录。", e));
                    }
                    @Override public void cancelled() {
                        Application.this.onApplicationException(
                            new ApplicationException("登录已取消。")
                        );
                    }
                });
            }
            @Override public void failed(Exception e) {
                Application.this.onApplicationException(
                    e instanceof ApplicationException
                    ? (ApplicationException) e
                    : new ApplicationException("发生内部错误，登录失败，请尝试重新登录。", e));
            }
            @Override public void cancelled() {
                Application.this.onApplicationException(
                    new ApplicationException("登录已取消。")
                );
            }
        });
    }

    public void onApplicationException(ApplicationException e) {
        Platform.runLater(() -> {
            Application.this._loginSceneController.onLoginError(e);
            Application.this._mainStage.setScene(Application.this._loginScene);
        });
    }

    private Stage _mainStage;

    private Scene _loginScene;
    private Scene _progressScene;

    private LoginSceneController    _loginSceneController;
    private ProgressSceneController _progressSceneController;

    private void loadFonts() throws Exception{
        var fonts = new String[] {
            "DengXian.ttf",
            "Ubuntu.ttf"
        };
        for (var font : fonts)
            Font.loadFont(
                this.getClass()
                    .getResource("/fonts/" + font)
                    .toExternalForm(),
                0
            );
    }

    private void loadScenes() throws Exception {
        this.loadLoginScene();
        this.loadProgressScene();
    }

    private void loadLoginScene() throws Exception {
        var loader = new FXMLLoader(this.getClass().getResource("login.fxml"));
        loader.load();
        this._loginSceneController = loader.getController();
        this._loginScene = new Scene(
            loader.getRoot(),
            Application.WINDOW_WIDTH,
            Application.WINDOW_HEIGHT
        );
    }

    private void loadProgressScene() throws Exception {
        var loader = new FXMLLoader(this.getClass().getResource("progress.fxml"));
        loader.load();
        this._progressSceneController = loader.getController();
        this._progressScene = new Scene(
            loader.getRoot(),
            Application.WINDOW_WIDTH,
            Application.WINDOW_HEIGHT
        );
    }

    public static Application getInstance() {
        return Application._instance;
    }

    private static Application _instance;

    private static final int WINDOW_WIDTH  = 450;
    private static final int WINDOW_HEIGHT = 600;
}

