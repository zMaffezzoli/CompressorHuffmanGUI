package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.Compressor;
import com.mycompany.huffman.model.LinhaTabela;

import com.mycompany.huffman.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

import java.util.Map;

public class MainControlador {

    // Componentes da Interface
    @FXML private TabPane painelAbasEntrada;
    @FXML private Tab abaTexto;
    @FXML private TextArea areaTexto;
    @FXML private Label rotuloArquivo;

    @FXML private TableView<LinhaTabela> tabelaFreq;
    @FXML private TableColumn<LinhaTabela, String> colSimboloFreq;
    @FXML private TableColumn<LinhaTabela, Integer> colFrequencia;

    @FXML private TableView<LinhaTabela> tabelaCodigos;
    @FXML private TableColumn<LinhaTabela, String> colSimboloCodigo;
    @FXML private TableColumn<LinhaTabela, String> colCodigo;

    @FXML private TextArea saidaBinaria;

    @FXML private StackPane viewportArvore;
    @FXML private Pane painelArvore;

    // Variáveis de controle
    private File arquivoSelecionado;
    private VisualizadorArvore visualizadorArvore;

    @FXML
    public void initialize() {
        configurarTabelas();
        // Inicializa o visualizador passando os componentes da GUI necessários
        this.visualizadorArvore = new VisualizadorArvore(painelArvore, viewportArvore);
    }

    private void configurarTabelas() {
        // Tabela frequencia
        colSimboloFreq.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colFrequencia.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        // Tabela codigo gerado
        colSimboloCodigo.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Usa a classe para estilizar e organizar as células das linhas das tabelas
        FormatacaoTabela.configurarColunasSimbolo(colSimboloFreq, colSimboloCodigo);

        tabelaFreq.setSelectionModel(null);
        tabelaCodigos.setSelectionModel(null);
    }

    @FXML
    private void acaoLimparTexto() {
        areaTexto.clear();
        areaTexto.requestFocus();
    }

    @FXML
    private void acaoColarExemplo() {
        areaTexto.setText("BANANA");
    }

    @FXML
    private void acaoSelecionarArquivo() {
        // Escreve o nome do arquivo selecionado
        FileChooser seletorArquivo = new FileChooser();
        seletorArquivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));

        arquivoSelecionado = seletorArquivo.showOpenDialog(null);
        if (arquivoSelecionado != null) rotuloArquivo.setText(arquivoSelecionado.getName());
    }

    @FXML
    private void acaoProcessar() {
        // Classe principal para executar tudo no front (recebe o onAction)

        // Trata possiveis erros de leitura do arquivo
        try {
            String texto = obterTextoEntrada();

            if (texto == null || texto.isEmpty()) {
                saidaBinaria.setText("Por favor, insira um texto ou selecione um arquivo que não esteja vazio.");
                return;
            }

            // Caso o arquivo não seja vazio, instancia as classes do algoritmo
            Compressor compressor = new Compressor(texto);
            saidaBinaria.setText(compressor.getBinario());

            atualizarTabelas(compressor);

            // Passamos a arvore gerada para a classe especializada para desenhá-la
            if (compressor.getArvore() != null) {
                visualizadorArvore.desenharArvore(compressor.getArvore().getRaiz());
            }

        } catch (Exception e) {
            e.printStackTrace();
            saidaBinaria.setText("Erro: " + e.getMessage());
        }
    }

    // Retorna o texto recebido (seja ele do textarea ou do arquivo)
    private String obterTextoEntrada() throws IOException {
        String texto = "";
        if (painelAbasEntrada.getSelectionModel().getSelectedItem() == abaTexto) {
            // Para solidificar o código
            Leitor leitor = new LeitorString(areaTexto.getText());

            // Conteudo do textarea
            texto = leitor.lerConteudo();

        } else if (arquivoSelecionado != null) {
            Leitor leitor = new LeitorArquivo(arquivoSelecionado);

            // Conteudo do arquivo
            texto = leitor.lerConteudo();
            if (texto != null) texto = texto.replaceAll("[\\r\\n]+$", "");
        }
        return texto;
    }

    private void atualizarTabelas(Compressor compressor) {
        ObservableList<LinhaTabela> dadosTabela = FXCollections.observableArrayList();
        Map<Character, Integer> mapaFreq = compressor.getTabelaFrequencia().getTabela();
        Map<String, String> mapaBinario = compressor.getTabelaBinaria().getTabela();

        // entry.getKey é comum das duas tabelas (o caracter em si)
        for (Map.Entry<Character, Integer> entrada : mapaFreq.entrySet()) {
            // Tratamento dos caracteres especiais na visualização das tabelas
            String simbolo = String.valueOf(entrada.getKey())
                    .replace("\n", "\\n").replace("\r", "\\r")
                    .replace("\t", "\\t").replace(" ", "space"); // Mantive "space" pois FormatacaoTabela verifica "space"

            dadosTabela.add(new LinhaTabela(simbolo, entrada.getValue(), mapaBinario.get(String.valueOf(entrada.getKey()))));
        }

        // Ordena as tabelas pela frequencia dos caracteres
        dadosTabela.sort((r1, r2) -> {
            int comparacaoFreq = Integer.compare(r2.getFrequency(), r1.getFrequency());
            return (comparacaoFreq != 0) ? comparacaoFreq : r1.getSymbol().compareTo(r2.getSymbol());
        });

        tabelaFreq.setItems(dadosTabela);
        tabelaCodigos.setItems(dadosTabela);
    }
}