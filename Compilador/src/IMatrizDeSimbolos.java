public interface IMatrizDeSimbolos {
    Token getTokenNaPosicao(int linha, int coluna);
    void alocarToken(Token tokenASerSalvo);
    Token buscarToken(String nome);
    int getLinhaAtual();
    int getColunaAtual();
    int getLinhaMax();
    int getColunaMax();
}
