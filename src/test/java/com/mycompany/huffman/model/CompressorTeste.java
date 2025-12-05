package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;

public class CompressorTeste {

    @Test
    public void main() {
        String texto = "Pindamonhangaba";
        Compressor compressor = new Compressor(texto);
        System.out.println(compressor.getBinario());
    }
}

