module coursex {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.jfoenix;
    requires httpcore5;
    requires httpclient5;

    exports coursex;
    opens   coursex to javafx.fxml;
}
