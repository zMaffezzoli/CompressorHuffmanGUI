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
        // Configura as colunas das tabelas
        colSymbolFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        colSymbolCode.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Remove a seleção padrão para estética
        freqTable.setSelectionModel(null);
        codeTable.setSelectionModel(null);

        // Configura o Zoom e o Pan (Arrastar)
        setupZoomAndPan();
    }

    private void setupZoomAndPan() {
        // 1. Clipping: Garante que a árvore não desenhe fora da área visível (caixa cinza)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(treeViewport.widthProperty());
        clip.heightProperty().bind(treeViewport.heightProperty());
        treeViewport.setClip(clip);

        // 2. Lógica de Scroll (ZOOM) - Otimizada para Linux/Windows
        treeViewport.setOnScroll(event -> {
            // Só executa se a tecla CTRL estiver pressionada
            if (event.isControlDown()) {
                // Impede que a página/scrollpane role junto
                event.consume();

                double deltaY = event.getDeltaY();

                // Proteção: Alguns trackpads enviam delta 0 (movimento horizontal), ignoramos.
                if (deltaY == 0) {
                    return;
                }

                // Lógica Simplificada:
                // Se deltaY positivo (scroll p/ cima) -> Aumenta 10% (x 1.1)
                // Se deltaY negativo (scroll p/ baixo) -> Diminui 10% (x 0.9)
                double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;

                // Aplica a escala no Pane interno
                treePane.setScaleX(treePane.getScaleX() * zoomFactor);
                treePane.setScaleY(treePane.getScaleY() * zoomFactor);
            }
        });

        // 3. Lógica de Clique (Início do Arrastar - Pan)
        treePane.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = treePane.getTranslateX();
            translateAnchorY = treePane.getTranslateY();
            treeViewport.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        // 4. Lógica de Arrastar (Movimento do Mouse)
        treePane.setOnMouseDragged(event -> {
            // Calcula o deslocamento
            treePane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            treePane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        });

        // 5. Soltar o Mouse
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
            // 1. Obter o texto da aba correta
            String text = "";
            if (inputTabPane.getSelectionModel().getSelectedItem() == tabText) {
                text = inputArea.getText();
            } else if (selectedFile != null) {
                TextReader reader = new FileTextReader(selectedFile);
                text = reader.readContent();
            }

            // Validação simples
            if (text == null || text.isEmpty()) {
                binaryOutput.setText("Por favor, insira um texto ou selecione um arquivo.");
                return;
            }

            // 2. BACKEND: Instancia sua classe Compressor (que gera tudo)
            Compressor compressor = new Compressor(text);

            // 3. Exibir o resultado Binário na tela
            binaryOutput.setText(compressor.getBinario());

            // 4. Popular as tabelas (Cruzando Frequência com Código Binário)
            ObservableList<HuffmanRow> tableData = FXCollections.observableArrayList();

            Map<Character, Integer> freqMap = compressor.getTabelaFrequencia().getTabela();
            Map<String, String> binaryMap = compressor.getTabelaBinaria().getTabela();

            for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                String symbol = String.valueOf(entry.getKey());
                Integer frequency = entry.getValue();

                // Pega o código binário correspondente ao símbolo
                String code = binaryMap.get(symbol);

                tableData.add(new HuffmanRow(symbol, frequency, code));
            }

            freqTable.setItems(tableData);
            codeTable.setItems(tableData);

            // 5. Desenhar a Árvore Visual
            if (compressor.getArvore() != null) {
                drawTree(compressor.getArvore().getRaiz());
            }

        } catch (Exception e) {
            e.printStackTrace();
            binaryOutput.setText("Erro: " + e.getMessage());
        }
    }

    // Método de desenho adaptado para sua classe 'No'
    private void drawTree(No root) {
        // Limpa desenhos anteriores
        treePane.getChildren().clear();

        // Reseta posição e zoom
        treePane.setTranslateX(0);
        treePane.setTranslateY(0);
        treePane.setScaleX(1);
        treePane.setScaleY(1);

        if (root != null) {
            // Inicia o desenho recursivo
            drawTreeRecursive(root, 0, 50, 200);
        }
    }

    private void drawTreeRecursive(No node, double x, double y, double hGap) {
        // --- Desenha Linhas para os Filhos ---

        // Filho Esquerdo
        if (node.getFilho_esquerdo() != null) {
            double childX = x - hGap;
            double childY = y + 100; // Distância vertical entre níveis

            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_esquerdo(), childX, childY, hGap * 0.6);
        }

        // Filho Direito
        if (node.getFilho_direito() != null) {
            double childX = x + hGap;
            double childY = y + 100;

            Line line = new Line(x, y, childX, childY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_direito(), childX, childY, hGap * 0.6);
        }

        // --- Desenha o Nó (Círculo) ---
        Circle circle = new Circle(x, y, 25);

        boolean isLeaf = node.isFolha();
        // Cores diferentes para folha (Vermelho) e nó interno (Azul Escuro)
        circle.setFill(isLeaf ? Color.web("#e74c3c") : Color.web("#34495e"));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // --- Desenha o Texto ---
        String labelText;
        if (isLeaf) {
            // Se for folha, mostra: Letra + Frequência
            labelText = node.getCaracter() + "\n" + node.getFrequencia();
        } else {
            // Se for nó interno, mostra apenas Frequência (para não poluir)
            labelText = String.valueOf(node.getFrequencia());
        }

        Text text = new Text(labelText);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setX(x - 5);
        text.setY(y + 5);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // Ajuste fino da posição do texto se for folha (por causa da quebra de linha)
        if (isLeaf) text.setY(y);

        treePane.getChildren().addAll(circle, text);
    }
}