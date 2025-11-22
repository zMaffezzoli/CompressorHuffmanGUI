package com.mycompany;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Cena inicial
        TextField campoTexto = new TextField();
        Button botaoMostrar = new Button("Mostrar");

        VBox layoutInput = new VBox(10, new Label("Digite algo:"), campoTexto, botaoMostrar);
        layoutInput.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Cena final (texto em vermelho)
        Label labelResultado = new Label();
        labelResultado.setTextFill(Color.RED);
        labelResultado.setFont(Font.font("SansSerif", 22));
        VBox layoutResultado = new VBox(labelResultado);
        layoutResultado.setStyle("-fx-alignment: center;");

        Scene cenaInput = new Scene(layoutInput, 400, 200);
        Scene cenaResultado = new Scene(layoutResultado, 400, 200);

        botaoMostrar.setOnAction(e -> {
            String texto = campoTexto.getText().trim();
            if (!texto.isEmpty()) {
                labelResultado.setText(texto);
                stage.setScene(cenaResultado);
            }
        });

        stage.setTitle("Mostrar em Vermelho (JavaFX)");
        stage.setScene(cenaInput);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
