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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
            // 1. Obter o texto da aba selecionada
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

            // 2. BACKEND: Gera a árvore e as tabelas
            Compressor compressor = new Compressor(text);

            // 3. Exibir o resultado Binário
            binaryOutput.setText(compressor.getBinario());

            // 4. PREPARAR DADOS PARA AS TABELAS
            ObservableList<HuffmanRow> tableData = FXCollections.observableArrayList();

            Map<Character, Integer> freqMap = compressor.getTabelaFrequencia().getTabela();
            Map<String, String> binaryMap = compressor.getTabelaBinaria().getTabela();

            // Popula a lista inicial
            for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                String symbol = String.valueOf(entry.getKey());
                Integer frequency = entry.getValue();
                String code = binaryMap.get(symbol);

                tableData.add(new HuffmanRow(symbol, frequency, code));
            }

            // --- ORDENAÇÃO UNIFICADA ---
            // Como as duas tabelas devem ter a mesma ordem, ordenamos a lista única uma vez.
            tableData.sort((r1, r2) -> {
                // 1. Compara Quantidade (Do MAIOR para o MENOR)
                // Usamos r2.getFrequency() primeiro para ordem decrescente
                int freqCompare = Integer.compare(r2.getFrequency(), r1.getFrequency());

                // Se a quantidade for diferente, já retorna quem ganha
                if (freqCompare != 0) {
                    return freqCompare;
                }

                // 2. Critério de Desempate: Ordem Alfabética (A -> Z)
                return r1.getSymbol().compareTo(r2.getSymbol());
            });

            // Aplica a MESMA lista ordenada para as DUAS tabelas
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

    private void drawTree(No root) {
        treePane.getChildren().clear();

        // Reseta posição e zoom
        treePane.setTranslateX(0);
        treePane.setTranslateY(0);
        treePane.setScaleX(1);
        treePane.setScaleY(1);

        if (root != null) {
            // --- CÁLCULO DINÂMICO DE ESPAÇAMENTO ---
            int profundidade = getAlturaArvore(root);

            // Lógica: Cada nó folha precisa de uns 70px de espaço mínimo (largura da cápsula + margem).
            // A largura inicial necessária é: 70 * 2^(profundidade - 2)
            // Isso garante que, ao dividir por 2 várias vezes, o último nível ainda tenha ~70px.

            double espacoMinimoFolha = 75.0;
            double initialGap = espacoMinimoFolha * Math.pow(2, Math.max(1, profundidade - 2));

            // Inicia o desenho com o gap calculado
            drawTreeRecursive(root, 0, 50, initialGap);
        }
    }

    private void drawTreeRecursive(No node, double x, double y, double hGap) {
        // --- 1. Desenha as Linhas (Arestas) ---
        double nextGap = hGap * 0.5;

        // Altura fixa da cápsula (usada para calcular onde a linha conecta)
        double capsuleHeight = 52;

        if (node.getFilho_esquerdo() != null) {
            double childX = x - hGap;
            double childY = y + 100;

            // Ajuste: A linha sai do centro inferior da cápsula atual
            // e vai até o centro superior da cápsula filha
            Line line = new Line(x, y + (capsuleHeight/2), childX, childY - (capsuleHeight/2));
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_esquerdo(), childX, childY, nextGap);
        }

        if (node.getFilho_direito() != null) {
            double childX = x + hGap;
            double childY = y + 100;

            Line line = new Line(x, y + (capsuleHeight/2), childX, childY - (capsuleHeight/2));
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_direito(), childX, childY, nextGap);
        }

        // --- 2. Configura o Texto ---
        String rawChar = node.getCaracter() == null ? "" : node.getCaracter();

        // Sanitização (Enter, Tab, etc)
        String charText = rawChar
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String freqText = String.valueOf(node.getFrequencia());
        String fullLabel = charText + "\n" + freqText;

        Text text = new Text(fullLabel);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setFont(Font.font("System", FontWeight.BOLD, 13));
        text.setFill(Color.WHITE);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        text.setWrappingWidth(0);

        // --- 3. Cria a Cápsula ---
        double textWidth = text.getLayoutBounds().getWidth();
        // Largura dinâmica (mínimo 52, ou tamanho do texto + 30px de margem)
        double capsuleWidth = Math.max(capsuleHeight, textWidth + 30);

        Rectangle capsule = new Rectangle();
        capsule.setWidth(capsuleWidth);
        capsule.setHeight(capsuleHeight);
        capsule.setArcWidth(capsuleHeight);
        capsule.setArcHeight(capsuleHeight);

        // Centraliza
        capsule.setX(x - (capsuleWidth / 2));
        capsule.setY(y - (capsuleHeight / 2));

        // Cores
        boolean isLeaf = node.isFolha();
        capsule.setFill(isLeaf ? Color.web("#e74c3c") : Color.web("#34495e"));
        capsule.setStroke(Color.BLACK);
        capsule.setStrokeWidth(2);

        // --- 4. Posiciona o Texto ---
        text.setX(x - (textWidth / 2));
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        treePane.getChildren().addAll(capsule, text);
    }

    // Metodo auxiliar para calcular a profundidade da arvore
    private int getAlturaArvore(No node) {
        if (node == null) return 0;
        int esquerda = getAlturaArvore(node.getFilho_esquerdo());
        int direita = getAlturaArvore(node.getFilho_direito());
        return Math.max(esquerda, direita) + 1;
    }
}