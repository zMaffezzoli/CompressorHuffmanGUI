package com.mycompany.huffman.model;

import java.util.HashMap;
import java.util.Map;

public class FrequencyTable {
    private String texto;
    private Map<Character, Integer> table = new HashMap<>();

    public Map<Character, Integer> getTable() {
        return table;
    }

    public void setTable(Map<Character, Integer> map) {
        this.table = map;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public FrequencyTable(String texto) {
        this.texto = texto;
        this.createTable();
    }

    private void createTable() {
        for  (char c : this.texto.toCharArray()) {
            table.put(c, table.getOrDefault(c, 0) + 1);
        }
    }
}
