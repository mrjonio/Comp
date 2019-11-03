import java.util.ArrayList;

public interface IMatrizDeSimbolos {
    Token getTokenNaPosicao(int linha, int coluna);
    void alocarToken(Token tokenASerSalvo);
    Token buscarToken(int linha, int coluna);
    int getLinhaAtual();
    int getColunaAtual();
    int getLinhaMax();
    int getColunaMax();
    public void addFuncao(Token funcao) throws JaDeclaradoError;
    public Token buscarVariavel(String nomeDaVariavel);
    public Token buscarFuncao(String nomeDaFuncao);
    public void addVariavel(Token variavel) throws JaDeclaradoError;
    public ArrayList<Token> funcs();
    public ArrayList<Token> vars();
}
