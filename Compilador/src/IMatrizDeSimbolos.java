public interface IMatrizDeSimbolos {
    Token getTokenNaPosicao(int linha, int coluna);
    void alocarToken(Token tokenASerSalvo);
    Token buscarToken(int linha, int coluna);
    int getLinhaAtual();
    int getColunaAtual();
    int getLinhaMax();
    int getColunaMax();
}
