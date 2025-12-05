package com.mycompany.huffman.model;

public class Compressor {
    private String texto;
    private TabelaFrequencia tabelaFrequencia;
    private Arvore arvore;
    private TabelaBinaria tabelaBinaria;
    private String binario = "";

    public Arvore getArvore() {
        return arvore;
    }

    public void setArvore(Arvore arvore) {
        this.arvore = arvore;
    }

    public String getBinario() {
        return binario;
    }

    public void setBinario(String binario) {
        this.binario = binario;
    }

    public TabelaBinaria getTabelaBinaria() {
        return tabelaBinaria;
    }

    public void setTabelaBinaria(TabelaBinaria tabelaBinaria) {
        this.tabelaBinaria = tabelaBinaria;
    }

    public TabelaFrequencia getTabelaFrequencia() {
        return tabelaFrequencia;
    }

    public void setTabelaFrequencia(TabelaFrequencia tabelaFrequencia) {
        this.tabelaFrequencia = tabelaFrequencia;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Compressor(String texto) {
        this.texto = texto;
        this.tabelaFrequencia = new TabelaFrequencia(texto);
        this.arvore = new Arvore(tabelaFrequencia);
        this.tabelaBinaria = new TabelaBinaria(arvore);
        montaBinario();
    }

    private void montaBinario() {
        for  (char c : this.texto.toCharArray()) {
            this.binario += this.tabelaBinaria.getTabela().get(String.valueOf(c)) + " ";
        }
    }
}
