public class Token {
    private String nome;
    private String lexema;
    private int linha;
    private String valor;
    private int linhaMatriz;
    private int colunaMatriz;

    public Token(String nome, String lexema, int linha, String valor, int linhaMatriz, int colunaMatriz) {
        this.nome = nome;
        this.lexema = lexema;
        this.linha = linha;
        this.valor = valor;
        this.linhaMatriz = linhaMatriz;
        this.colunaMatriz = colunaMatriz;
    }

    public String getNome() {
        return nome;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }

    public int getLinhaMatriz() {
        return linhaMatriz;
    }

    public void setLinhaMatriz(int linhaMatriz) {
        this.linhaMatriz = linhaMatriz;
    }

    public int getColunaMatriz() {
        return colunaMatriz;
    }

    public void setColunaMatriz(int colunaMatriz) {
        this.colunaMatriz = colunaMatriz;
    }

    public String getValor() {
        return valor;
    }


}
