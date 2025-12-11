package com.mycompany.huffman.app;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;

public class HuffmanApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o FXML
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainTela.fxml"));
        Scene scene = new Scene(root);

        // Adiciona O CSS
        String cssArvore = getClass().getResource("/styles/arvore.css").toExternalForm();
        String cssBotoes = getClass().getResource("/styles/botoes.css").toExternalForm();
        String cssInput = getClass().getResource("/styles/input.css").toExternalForm();
        String cssTabelas = getClass().getResource("/styles/tabelas.css").toExternalForm();
        String cssTextos = getClass().getResource("/styles/textos.css").toExternalForm();
        String css = getClass().getResource("/styles/variaveis.css").toExternalForm();
        scene.getStylesheets().addAll(Arrays.asList(cssArvore, cssBotoes, cssInput, cssTabelas, cssTextos, css));

        // Seta as configurações padrões de tela
        stage.setTitle("Compressor de Huffman");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Inicializador JavaFx
    public static void main(String[] args) {
        launch(args);
    }
}