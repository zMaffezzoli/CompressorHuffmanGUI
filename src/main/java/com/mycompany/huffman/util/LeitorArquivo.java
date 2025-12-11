package com.mycompany.huffman.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LeitorArquivo extends Leitor {
    private final File file;

    public LeitorArquivo(File file) {
        this.file = file;
    }

    @Override
    public String lerConteudo() throws IOException {
        if (file == null || !file.exists()) throw new IOException("Arquivo inválido.");
        return Files.readString(file.toPath());
    }
}