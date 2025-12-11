package com.mycompany.huffman.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LeitorArquivo extends Leitor {
    private final File arquivo;

    public LeitorArquivo(File file) {
        this.arquivo = file;
    }

    @Override
    public String lerConteudo() throws IOException {
        if (arquivo == null || !arquivo.exists()) throw new IOException("Arquivo inválido.");
        return Files.readString(arquivo.toPath());
    }
}