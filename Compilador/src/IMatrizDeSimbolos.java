public interface IMatrizDeSimbolos {
    Token getTokenNaPosicao(int linha, int coluna);
    void alocarToken(Token tokenASerSalvo);
    int getLinhaAtual();
    int getColunaAtual();
    int getLinhaMax();
    int getColunaMax();
}
