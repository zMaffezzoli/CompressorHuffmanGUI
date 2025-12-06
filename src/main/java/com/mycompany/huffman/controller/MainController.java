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

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
            // 1. MAPA DE COORDENADAS
            // Vamos guardar a posição X calculada de cada nó aqui
            Map<No, Double> xPositions = new HashMap<>();

            // Um contador para saber em qual "coluna" estamos desenhando a próxima folha
            AtomicInteger leafIndex = new AtomicInteger(0);

            // Distância fixa entre cada folha (Isso define a largura da árvore)
            double leafSpacing = 85.0;

            // Calcula matematicamente onde cada nó deve ficar
            calculatePositions(root, xPositions, leafIndex, leafSpacing);

            // 2. DESENHO
            // Agora desenhamos usando as coordenadas exatas do mapa.
            // O Y continua sendo calculado por nível (nível * 100)

            // Centraliza a raiz na tela inicialmente (opcional, mas ajuda)
            double rootX = xPositions.get(root);
            treePane.setTranslateX(-rootX + 400); // Tenta jogar a raiz pro meio da tela

            drawTreeRecursive(root, 50, xPositions);
        }
    }

    // Agora recebe o Map de posições pré-calculadas
    private void drawTreeRecursive(No node, double y, Map<No, Double> xPositions) {
        // Pega a posição X calculada para este nó
        double x = xPositions.get(node);

        // --- 1. Desenha as Linhas ---
        if (node.getFilho_esquerdo() != null) {
            double childX = xPositions.get(node.getFilho_esquerdo());
            double childY = y + 100;

            // Linha saindo do centro da cápsula (altura 52)
            Line line = new Line(x, y + 26, childX, childY - 26);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_esquerdo(), childY, xPositions);
        }

        if (node.getFilho_direito() != null) {
            double childX = xPositions.get(node.getFilho_direito());
            double childY = y + 100;

            Line line = new Line(x, y + 26, childX, childY - 26);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            line.toBack();
            treePane.getChildren().add(line);

            drawTreeRecursive(node.getFilho_direito(), childY, xPositions);
        }

        // --- 2. Cria a Cápsula (Igual ao anterior) ---

        // Texto
        String rawChar = node.getCaracter() == null ? "" : node.getCaracter();
        String charText = rawChar.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        String freqText = String.valueOf(node.getFrequencia());
        String fullLabel = charText + "\n" + freqText;

        Text text = new Text(fullLabel);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setFont(Font.font("System", FontWeight.BOLD, 13));
        text.setFill(Color.WHITE);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        text.setWrappingWidth(0);

        // Dimensões
        double fixedHeight = 52;
        double textWidth = text.getLayoutBounds().getWidth();
        double capsuleWidth = Math.max(fixedHeight, textWidth + 30);

        Rectangle capsule = new Rectangle();
        capsule.setWidth(capsuleWidth);
        capsule.setHeight(fixedHeight);
        capsule.setArcWidth(fixedHeight);
        capsule.setArcHeight(fixedHeight);

        // Posiciona (X vem do mapa, Y vem do parametro recursivo)
        capsule.setX(x - (capsuleWidth / 2));
        capsule.setY(y - (fixedHeight / 2));

        // Cores
        boolean isLeaf = node.isFolha();
        capsule.setFill(isLeaf ? Color.web("#e74c3c") : Color.web("#34495e"));
        capsule.setStroke(Color.BLACK);
        capsule.setStrokeWidth(2);

        // Texto Centralizado
        text.setX(x - (textWidth / 2));
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        treePane.getChildren().addAll(capsule, text);
    }

    // Metodo recursivo que define o X de cada nó (Bottom-Up)
    private void calculatePositions(No node, Map<No, Double> xPositions, AtomicInteger leafIndex, double spacing) {
        if (node == null) return;

        // Se for folha, ela ganha a próxima posição disponível na fila
        if (node.isFolha()) {
            double x = leafIndex.getAndIncrement() * spacing;
            xPositions.put(node, x);
        } else {
            // Se não for folha, processa os filhos primeiro
            calculatePositions(node.getFilho_esquerdo(), xPositions, leafIndex, spacing);
            calculatePositions(node.getFilho_direito(), xPositions, leafIndex, spacing);

            // A posição do Pai é exatamente no meio dos filhos
            double leftX = xPositions.containsKey(node.getFilho_esquerdo()) ?
                    xPositions.get(node.getFilho_esquerdo()) : 0;

            double rightX = xPositions.containsKey(node.getFilho_direito()) ?
                    xPositions.get(node.getFilho_direito()) : leftX;

            // Se tiver os dois filhos, fica no meio. Se tiver só um, fica em cima dele.
            double myX = (leftX + rightX) / 2.0;
            xPositions.put(node, myX);
        }
    }
}

