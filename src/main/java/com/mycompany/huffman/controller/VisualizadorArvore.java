package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.No;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class VisualizadorArvore {
    private final Pane painelArvore;
    private final StackPane viewportArvore;

    // Controle do painel (Zoom e Arrastar)
    private double mouseAncoraX, mouseAncoraY;
    private double translacaoAncoraX, translacaoAncoraY;

    public VisualizadorArvore(Pane painelArvore, StackPane viewportArvore) {
        this.painelArvore = painelArvore;
        this.viewportArvore = viewportArvore;
        configurarZoomEArrastar();
    }

    private void configurarZoomEArrastar() {
        // Define a área de recorte para que a árvore não saia do quadrado visual
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(viewportArvore.widthProperty());
        recorte.heightProperty().bind(viewportArvore.heightProperty());
        viewportArvore.setClip(recorte);

        // Zoom com Scroll + Ctrl
        viewportArvore.setOnScroll(evento -> {
            if (evento.isControlDown()) {
                evento.consume();
                double deltaY = evento.getDeltaY();
                if (deltaY == 0) return;

                double fatorZoom = (deltaY > 0) ? 1.1 : 0.9;
                painelArvore.setScaleX(painelArvore.getScaleX() * fatorZoom);
                painelArvore.setScaleY(painelArvore.getScaleY() * fatorZoom);
            }
        });

        // Clique inicial para arrastar
        painelArvore.setOnMousePressed(evento -> {
            mouseAncoraX = evento.getSceneX();
            mouseAncoraY = evento.getSceneY();
            translacaoAncoraX = painelArvore.getTranslateX();
            translacaoAncoraY = painelArvore.getTranslateY();
            viewportArvore.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        // Movimento de arrastar
        painelArvore.setOnMouseDragged(evento -> {
            painelArvore.setTranslateX(translacaoAncoraX + evento.getSceneX() - mouseAncoraX);
            painelArvore.setTranslateY(translacaoAncoraY + evento.getSceneY() - mouseAncoraY);
        });

        // Soltar o mouse
        painelArvore.setOnMouseReleased(evento -> viewportArvore.setCursor(javafx.scene.Cursor.DEFAULT));
    }

    public void desenharArvore(No raiz) {
        painelArvore.getChildren().clear();
        painelArvore.setTranslateX(0);
        painelArvore.setTranslateY(0);
        painelArvore.setScaleX(1);
        painelArvore.setScaleY(1);

        if (raiz != null) {
            Map<No, Double> posicoesX = new HashMap<>();
            AtomicInteger indiceFolha = new AtomicInteger(0);
            double espacamentoFolha = 85.0;

            calcularPosicoes(raiz, posicoesX, indiceFolha, espacamentoFolha);

            double raizX = posicoesX.get(raiz);
            // Centraliza a raiz inicialmente (ajuste manual de offset se necessário)
            painelArvore.setTranslateX(-raizX + 400);

            desenharArvoreRecursivo(raiz, 50, posicoesX);
        }
    }

    private void desenharArvoreRecursivo(No no, double y, Map<No, Double> posicoesX) {
        double x = posicoesX.get(no);

        // Desenha as linhas para os filhos
        if (no.getFilhoEsquerdo() != null) {
            desenharConexao(x, y, posicoesX.get(no.getFilhoEsquerdo()), y + 100);
            desenharArvoreRecursivo(no.getFilhoEsquerdo(), y + 100, posicoesX);
        }
        if (no.getFilhoDireito() != null) {
            desenharConexao(x, y, posicoesX.get(no.getFilhoDireito()), y + 100);
            desenharArvoreRecursivo(no.getFilhoDireito(), y + 100, posicoesX);
        }

        desenharConteudoNo(no, x, y);
    }

    private void desenharConexao(double x1, double y1, double x2, double y2) {
        // Desenha as linhas que unem pais e filhos
        Line linha = new Line(x1, y1 + 26, x2, y2 - 26);
        linha.setStroke(Color.GRAY);
        linha.setStrokeWidth(2);
        linha.toBack();
        painelArvore.getChildren().add(linha);
    }

    private void desenharConteudoNo(No no, double x, double y) {
        // Desenha o texto na tela
        String caracterBruto = no.getCaracter() == null ? "" : no.getCaracter();

        // Tratamento dos caracteres especiais para exibição
        String textoCaracter = caracterBruto.replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t").replace(" ", "espaço");

        String rotuloCompleto = textoCaracter + "\n" + no.getFrequencia();

        Text texto = new Text(rotuloCompleto);
        texto.setBoundsType(TextBoundsType.VISUAL);

        // Verifica se é espaço para estilização personalizada
        if (no.isFolha() && textoCaracter.equals("espaço")) {
            texto.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 13));
        } else {
            texto.setFont(Font.font("System", FontWeight.BOLD, 13));
        }

        texto.setFill(Color.WHITE);
        texto.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Formato da borda (Cápsula)
        double alturaFixa = 52;
        double larguraTexto = texto.getLayoutBounds().getWidth();
        double larguraCapsula = Math.max(alturaFixa, larguraTexto + 30);

        // Desenha a borda
        Rectangle capsula = new Rectangle();
        capsula.setWidth(larguraCapsula);
        capsula.setHeight(alturaFixa);
        capsula.setArcWidth(alturaFixa);
        capsula.setArcHeight(alturaFixa);
        capsula.setX(x - (larguraCapsula / 2));
        capsula.setY(y - (alturaFixa / 2));

        // Vermelho para folhas, Azul escuro para nós internos
        capsula.setFill(no.isFolha() ? Color.web("#e74c3c") : Color.web("#34495e"));
        capsula.setStroke(Color.BLACK);
        capsula.setStrokeWidth(2);

        texto.setX(x - (larguraTexto / 2));
        texto.setY(y + texto.getLayoutBounds().getHeight() / 10);

        painelArvore.getChildren().addAll(capsula, texto);
    }

    private void calcularPosicoes(No no, Map<No, Double> posicoesX, AtomicInteger indiceFolha, double espacamento) {
        // Calcula a distancia entre os nós para que a visualização fique correta
        // sem sobreposição de nós

        if (no == null) return;

        if (no.isFolha()) {
            double x = indiceFolha.getAndIncrement() * espacamento;
            posicoesX.put(no, x);
        } else {
            // Recursividade para os nós filhos
            calcularPosicoes(no.getFilhoEsquerdo(), posicoesX, indiceFolha, espacamento);
            calcularPosicoes(no.getFilhoDireito(), posicoesX, indiceFolha, espacamento);

            double esquerdaX = posicoesX.getOrDefault(no.getFilhoEsquerdo(), 0.0);

            // Se tiver filho direito, pega a posição, senão usa a do esquerdo (caso raro em Huffman mas bom prevenir)
            double direitaX = posicoesX.containsKey(no.getFilhoDireito()) ?
                    posicoesX.get(no.getFilhoDireito()) : esquerdaX;

            posicoesX.put(no, (esquerdaX + direitaX) / 2.0);
        }
    }
}