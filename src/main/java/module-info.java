module com.example.networkproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.networkproject to javafx.fxml;
    exports com.example.networkproject;
    exports com.example.networkproject.controllers;
    opens com.example.networkproject.controllers to javafx.fxml;
}