package com.mycompany.huffman.util;

public class LeitorString extends Leitor {
    private final String content;

    public LeitorString(String content) {
        this.content = content;
    }

    @Override
    public String lerConteudo() {
        return content;
    }
}