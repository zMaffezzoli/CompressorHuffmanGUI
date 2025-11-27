package com.mycompany.huffman.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControllerResult {

    @FXML
    private Label labelResultado;

    public void setTexto(String texto) {
        labelResultado.setText(texto);
    }
}
