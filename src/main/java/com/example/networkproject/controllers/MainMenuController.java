package com.example.networkproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private Label textLabel;


    public void initialize(){
        textLabel.setText("");
    }

    public void OnStartButtonClick() throws IOException {
        textLabel.setText("Let's go!");


    }
}
