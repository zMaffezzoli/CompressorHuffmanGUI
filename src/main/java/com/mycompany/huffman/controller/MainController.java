package com.mycompany.huffman.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private TextField campoTexto;

    @FXML
    private void mostrarTexto() throws Exception {
        String texto = campoTexto.getText().trim();

        if (!texto.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/result.fxml"));
            Parent root = loader.load();

            ControllerResult controller = loader.getController();
            controller.setTexto(texto);

            Stage stage = (Stage) campoTexto.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
    }
}
