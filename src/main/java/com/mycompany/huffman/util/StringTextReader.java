package com.mycompany.huffman.util;

public class StringTextReader extends TextReader {
    private final String content;

    public StringTextReader(String content) {
        this.content = content;
    }

    @Override
    public String readContent() {
        return content;
    }
}