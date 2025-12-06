package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.Compressor;
import com.mycompany.huffman.model.HuffmanRow;
import com.mycompany.huffman.model.No;
import com.mycompany.huffman.util.FileTextReader;
import com.mycompany.huffman.util.TextReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Map;

public class MainController {

    @FXML private TabPane inputTabPane;
    @FXML private Tab tabText;
    @FXML private TextArea inputArea;
    @FXML private Label fileLabel;

    @FXML private TableView<HuffmanRow> freqTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolFreq;
    @FXML private TableColumn<HuffmanRow, Integer> colFreq;

    @FXML private TableView<HuffmanRow> codeTable;
    @FXML private TableColumn<HuffmanRow, String> colSymbolCode;
    @FXML private TableColumn<HuffmanRow, String> colCode;

    @FXML private TextArea binaryOutput;

    // ÁREA DA ÁRVORE
    @FXML private StackPane treeViewport;
    @FXML private Pane treePane;

    private File selectedFile;

    // Variáveis para controle de arrastar (Pan)
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        freqTable.setSelectionModel(null);
        codeTable.setSelectionModel(null);

        setupZoomAndPan();
    }

    private void setupZoomAndPan() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(treeViewport.widthProperty());
        clip.heightProperty().bind(treeViewport.heightProperty());
        treeViewport.setClip(clip);

        treeViewport.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 1 / zoomFactor;
                }
                treePane.setScaleX(treePane.getScaleX() * zoomFactor);
                treePane.setScaleY(treePane.getScaleY() * zoomFactor);
                event.consume();
            }
        });

        treePane.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = treePane.getTranslateX();
            translateAnchorY = treePane.getTranslateY();
            treeViewport.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        treePane.setOnMouseDragged(event -> {
            treePane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            treePane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        });

        treePane.setOnMouseReleased(event -> {
            treeViewport.setCursor(javafx.scene.Cursor.DEFAULT);
        });
    }

    @FXML
    private void handleClearText() {
        inputArea.clear();
        inputArea.requestFocus();
    }

    @FXML
    private void handlePasteExample() {
        inputArea.setText("BANANA");
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) fileLabel.setText(selectedFile.getName());
    }

    @FXML
    private void handleProcess() {
        try {
            // 1. Obter o texto
            String text = "";
            if (inputTabPane.getSelectionModel().getSelectedItem() == tabText) {
                text = inputArea.getText();
            } else if (selectedFile != null) {
                TextReader reader = new FileTextReader(selectedFile);
                text = reader.readContent();
            }

            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo.");
                return;
            }

            // 2. CHAMAR A SUA LÓGICA (BACKEND)
            Compressor compressor = new Compressor(text);

            // 3. Exibir o resultado Binário
            binaryOutput.setText(compressor.getBinario());

            // 4. Popular as tabelas
            ObservableList<HuffmanRow> tableData = FXCollections.observableArrayList();

            // Pegamos a tabela de frequência e a tabela binária para cruzar os dados
            Map<Character, Integer> freqMap = compressor.getTabelaFrequencia().getTabela();
            Map<String, String> binaryMap = compressor.getTabelaBinaria().getTabela();

            for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                String symbol = String.valueOf(entry.getKey());
                Integer frequency = entry.getValue();
                String code = binaryMap.get(symbol); // Pega o código binário correspondente

                tableData.add(new HuffmanRow(symbol, frequency, code));
            }

            freqTable.setItems(tableData);
            codeTable.setItems(tableData);

            // 5. Desenhar a Árvore Real
            // Usamos a raiz da árvore gerada pela sua lógica
            if (compressor.getArvore() != null) {
                drawTree(compressor.getArvore().getRaiz());
            }

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    // Método adaptado para aceitar sua classe 'No'
    private void drawTree(No root) {
        treePane.getChildren().clear();
        treePane.setTranslateX(0);
        treePane.setTranslateY(0);
        treePane.setScaleX(1);
        treePane.setScaleY(1);

        if (root != null) drawTreeRecursive(root, 0, 50, 200);
    }

    private void drawTreeRecursive(No node, double x, double y, double hGap) {
        // Verifica filhos usando os getters da sua classe No
        if (node.getFilho_esquerdo() != null) {
            double childX = x - hGap;
            double childY = y + 100;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);
            // Recursividade
            drawTreeRecursive(node.getFilho_esquerdo(), childX, childY, hGap * 0.6);
        }

        if (node.getFilho_direito() != null) {
            double childX = x + hGap;
            double childY = y + 100;
            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);
            // Recursividade
            drawTreeRecursive(node.getFilho_direito(), childX, childY, hGap * 0.6);
        }

        // Desenha o Nó
        Circle circle = new Circle(x, y, 25);

        // Se for folha, pinta de uma cor, se for nó interno, de outra
        // Usamos seu método isFolha()
        boolean isLeaf = node.isFolha();
        circle.setFill(isLeaf ? Color.web("#e74c3c") : Color.web("#34495e")); // Vermelho para folha, Azul escuro para interno
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // Texto do Label
        // Se for folha, mostra o Caracter. Se for nó interno, mostramos apenas a frequência para não poluir
        // (Já que sua classe No concatena strings tipo "a + b + c" nos nós internos)
        String labelText;
        if (isLeaf) {
            labelText = node.getCaracter() + "\n" + node.getFrequencia();
        } else {
            labelText = String.valueOf(node.getFrequencia());
        }

        Text text = new Text(labelText);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setX(x - 5);
        text.setY(y + 5);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // Pequeno ajuste de centralização dependendo se tem quebra de linha ou não
        if (isLeaf) text.setY(y);

        treePane.getChildren().addAll(circle, text);
    }
}