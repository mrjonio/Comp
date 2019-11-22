import java.util.ArrayList;

public class Token {
    private String nome;
    private String lexema;
    private int linha;
    private String valor;
    private int linhaMatriz;
    private int colunaMatriz;
    private Escopo escopo;
    private String retornoFuncao;
    private ArrayList<String> parametros;
    ArrayList<String> nomeParams;
    private boolean declarada;

    public Token(String nome, String lexema, int linha, String valor, int linhaMatriz, int colunaMatriz) {
        this.nome = nome;
        this.lexema = lexema;
        this.linha = linha;
        this.valor = valor;
        this.linhaMatriz = linhaMatriz;
        this.colunaMatriz = colunaMatriz;
        this.escopo = null;
        this.retornoFuncao = null;
        this.parametros = null;
        this.declarada = false;
        this.nomeParams = new ArrayList<>();

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

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setEscopo(Escopo escopo){
        this.escopo = escopo;
    }

    public Escopo getEscopo() {
        return this.escopo;
    }

    public String getRetornoFuncao() {
        return retornoFuncao;
    }

    public void setRetornoFuncao(String retornoFuncao) {
        this.retornoFuncao = retornoFuncao;
    }

    public ArrayList<String> getParametros() {
        return parametros;
    }

    public void setParametros(ArrayList<String> parametros) {
        this.parametros = parametros;
    }

    public boolean isDeclarada() {
        return declarada;
    }

    public void setDeclarada(boolean declarada) {
        this.declarada = declarada;
    }
}
