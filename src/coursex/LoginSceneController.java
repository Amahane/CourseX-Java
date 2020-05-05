package coursex;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class LoginSceneController {
    public void onLoginError(LoginException e) {
        this._errorLabel.setText(e.getMessage());
        this._errorLabel.setVisible(true);
    }

    @FXML private Label            _errorLabel;
    @FXML private JFXPasswordField _passwordField;
    @FXML private JFXTextField     _userNameField;

    @FXML private void onLoginButtonClicked(MouseEvent mouseEvent) {
        Application.getInstance().onLogin(
            this._userNameField.getText(),
            this._passwordField.getText()
        );
    }
}
