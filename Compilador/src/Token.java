public class Token {
    private String nome;
    private String lexema;
    private int linha;
    private String valor;

    public Token(String nome, String lexema, int linha, String valor) {
        this.nome = nome;
        this.lexema = lexema;
        this.linha = linha;
        this.valor = valor;
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

    public String getValor() {
        return valor;
    }
}
