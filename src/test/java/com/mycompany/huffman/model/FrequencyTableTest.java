package com.mycompany.huffman.model;

import org.junit.jupiter.api.Test;

public class FrequencyTableTest {

    @Test
    public void testCreateTable() {
        FrequencyTable tabela = new FrequencyTable("Pindamo nhang aba   ");
        System.out.println(tabela.getTable());
    }
}
