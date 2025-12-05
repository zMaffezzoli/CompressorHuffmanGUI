package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;


public class TabelaBinariaTeste {

    @Test
    public void main() {
        String texto = "Pindamonhangaba";
        TabelaFrequencia tabelaFrequencia = new TabelaFrequencia(texto);
        Arvore arvore = new Arvore(tabelaFrequencia);
        TabelaBinaria tabelaCodificada = new TabelaBinaria(arvore);
        System.out.println(tabelaCodificada.getTabela());
    }
}
