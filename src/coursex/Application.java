package coursex;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.core5.concurrent.FutureCallback;

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
                        for (var cookie : cookieStore.getCookies())
                            System.out.println(cookie.getName() + ": "+ cookie.getValue());
                    }
                    @Override public void failed(Exception e) {
                        Application.this.onLoginError(
                            e instanceof LoginException
                                ? (LoginException) e
                                : new LoginException("发生内部错误，登录失败，请尝试重新登录。", e));
                    }
                    @Override public void cancelled() {
                        Application.this.onLoginError(
                            new LoginException("登录已取消。")
                        );
                    }
                });
            }
            @Override public void failed(Exception e) {
                Application.this.onLoginError(
                    e instanceof LoginException
                    ? (LoginException) e
                    : new LoginException("发生内部错误，登录失败，请尝试重新登录。", e));
            }
            @Override public void cancelled() {
                Application.this.onLoginError(
                    new LoginException("登录已取消。")
                );
            }
        });
    }

    public void onLoginError(LoginException e) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Application.this._loginSceneController.onLoginError(e);
                Application.this._mainStage.setScene(Application.this._loginScene);
            }
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

