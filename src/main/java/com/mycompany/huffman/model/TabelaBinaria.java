package com.mycompany.huffman.model;

import java.util.HashMap;
import java.util.Map;

public class TabelaBinaria {
    private Map<String, String> tabela;

    public Map<String, String> getTabela() {
        return tabela;
    }

    public void setTabela(Map<String, String> tabela) {
        this.tabela = tabela;
    }

    public TabelaBinaria() {
        this.tabela = new HashMap<>();
    }

    public TabelaBinaria(Arvore arvore) {
        this.tabela = new HashMap<>();
        this.createTable(arvore.getRaiz(), "");
    }

    private void createTable(No no, String prefixo) {
        // Caso o nó não tenha filho
        if (no == null) return;

        // Quando chegar em uma folha
        if (no.isFolha()) {
            this.tabela.put(no.getCaracter(), prefixo);
            return;
        }

        // Utiliza recursividade para gerar os filhos abaixo
        createTable(no.getFilho_esquerdo(), prefixo + "0");
        createTable(no.getFilho_direito(), prefixo + "1");
    }
}
