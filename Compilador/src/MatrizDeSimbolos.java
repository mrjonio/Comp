public class MatrizDeSimbolos implements IMatrizDeSimbolos{
    private int linha;
    private int coluna;
    private Token[][] matriz;

    public MatrizDeSimbolos() {
        this.linha = 0;
        this.coluna = 0;
        this.matriz = new Token[100][100];

    }

    @Override
    public void alocarToken(Token tokenASerSalvo) {

        if (this.linha >= 100) {
            System.out.println("ACABOU O ESPACO DA MATRIZ");
        } else {
            this.matriz[this.linha][this.coluna] = tokenASerSalvo;
            this.coluna++;

            if (this.coluna >= 100) {
                this.coluna = 0;
                this.linha++;

            }
        }

    }

    @Override
    public Token getTokenNaPosicao(int linha, int coluna) {
        return this.matriz[linha][coluna];
    }

    @Override
    public int getLinhaAtual() {
        return this.linha;
    }

    @Override
    public int getColunaAtual() {
        return this.coluna;
    }

}
