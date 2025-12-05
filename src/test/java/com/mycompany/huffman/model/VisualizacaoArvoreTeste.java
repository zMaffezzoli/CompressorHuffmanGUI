package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;

public class VisualizacaoArvoreTeste {

    public void printArvore(No node) {
        printArvore(node, "", true);
    }

    public void printArvore(No no, String prefix, boolean isLeft) {
        if (no == null) return;

        System.out.println(prefix + (isLeft ? "└── " : "├── ")
                + (no.isFolha() ? "'" + no.getCaracter() + "'" : "")
                + " (" + no.getFrequencia() + ")");

        // prefixo dos filhos
        String childPrefix = prefix + (isLeft ? "    " : "│   ");

        printArvore(no.getFilho_esquerdo(), childPrefix, false);
        printArvore(no.getFilho_direito(), childPrefix, true);
    }

    @Test
    public void visualizarArvore() {
        TabelaFrequencia tabela = new TabelaFrequencia("Pindamonhangaba");
        Arvore arvore = new Arvore(tabela);

        System.out.println("Árvore de Huffman:");
        printArvore(arvore.getRaiz());
    }
}
