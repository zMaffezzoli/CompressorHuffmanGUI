package com.mycompany.huffman.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileTextReader extends TextReader {
    private final File file;

    public FileTextReader(File file) {
        this.file = file;
    }

    @Override
    public String readContent() throws IOException {
        if (file == null || !file.exists()) throw new IOException("Arquivo inválido.");
        return Files.readString(file.toPath());
    }
}