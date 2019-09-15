public interface IMatrizDeSimbolos {
    Token getTokenNaPosicao(int linha, int coluna);
    void alocaToken(Token tokenASerSalvo);
    IMatrizDeSimbolos criaMatriz(int tamanho);

}
