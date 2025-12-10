<div align="center">
    <h1>Compressor Huffman</h1>
</div>

Este projeto implementa o **Algoritmo de Huffman** em Java, com uma interface gráfica desenvolvida em **JavaFX** para visualização da Árvore de Huffman.

## Sobre o Projeto
O objetivo é permitir que o usuário:
- Insira um texto ou arquivo .txt;
- Veja o cálculo das frequências dos caracteres;
- Visualize a **árvore binária de Huffman** gerada;
- Visualize os **códigos binários** atribuídos a cada caractere;
- Realize a **codificação**  da mensagem.

A aplicação utiliza **Maven** como gerenciador de dependências e estrutura modular adequada ao Java 21.

---

## Tecnologias utilizadas
<div display="flex">
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java-Icon">
    <img src="https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white" alt="JavaFx-Icon">
    <img src="https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white" alt="ApacheMaven-Icon">
    <img src="https://img.shields.io/badge/JUnit5-%23f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a" alt="JUnit 5-Icon">
</div>

---

##  Estrutura Geral do Projeto

```
com.mycompany.huffman
 ├── app
 │    └── HuffmanApp.java        # Classe principal (JavaFX Application)
 ├── controller
 │    └── *.java                 # Controladores da interface FXML
 ├── model
 │    ├── Arvore.java            # Construção da árvore
 │    ├── Compressor.java        # Gerencia toda a compresão
 │    ├── LinhaTabela.java       # Classe auxiliar da interface
 │    ├── No.java                # Nó da árvore
 │    ├── TabelaBinaria.java     # Gera a tebela dos caracteres em binário
 │    └── TabelaFrequencia.java  # Gera a tebela de frequencia dos caracteres
 └── util
      ├── Leitor.java            # Classe abstrata para ler texto
      ├── LeitorArquivo.java     # Leitor do arquivo .txt
      └── LeitorString.java      # Leitor do texto recebido
```

---

##  Como Executar o Projeto
### Pré‑requisitos
- Java 21 instalado
- Maven instalado

### Executando via Maven
No diretório raiz:
```bash
mvn clean install
mvn javafx:run
```

---

##  Testes
O projeto utiliza **JUnit 5**. Para executar:
```bash
mvn test -Dtest=<arquivoDeTeste.java>
```

---

##  Visualização da Árvore de Huffman
A interface JavaFX plota a árvore binária dinamicamente, permitindo visualizar:
- Nós internos (somatórios de frequência)
- Nós folha (caracteres)
- Caminhos 0/1 gerados
