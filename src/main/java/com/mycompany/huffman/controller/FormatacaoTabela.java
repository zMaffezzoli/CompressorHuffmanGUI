package com.mycompany.huffman.controller;

import com.mycompany.huffman.model.LinhaTabela;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javafx.util.Callback;

public class FormatacaoTabela {

    public static void configurarColunasSimbolo(TableColumn<LinhaTabela, String>... columns) {
        // Puxa sempre que necessario
        Callback<TableColumn<LinhaTabela, String>, TableCell<LinhaTabela, String>> cellFactory = param -> new TableCell<>() {

            // Toda vez que rolamos a tela, verificamos quais linhas devem ser desenhar e qyais
            // devem ficar como null (pois, nao aparecem na tela, por conta da scroll)
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if ("space".equals(item)) {
                        setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 13));
                    } else {
                        setFont(Font.font("System", FontWeight.BOLD, 13));
                    }
                }
            }
        };

        // Adiciona todas as celulas das linhas que serão visiveis
        for (var col : columns) {
            col.setCellFactory(cellFactory);
        }
    }
}