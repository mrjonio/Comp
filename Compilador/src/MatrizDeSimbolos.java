public class MatrizDeSimbolos implements IMatrizDeSimbolos{
    private int linha;
    private int coluna;
    private Token[][] matriz;
    private int tamMaxL;
    private int tamMaxC;

    public MatrizDeSimbolos(int tamL, int tamC) {
        this.linha = 0;
        this.coluna = 0;
        this.matriz = new Token[tamL][tamC];
        tamMaxL = tamL;
        tamMaxC = tamC;

    }

    @Override
    public void alocarToken(Token tokenASerSalvo) {

        if (this.linha >= this.matriz.length) {
            System.out.println("ACABOU O ESPACO DA MATRIZ");
        } else {
            if (this.coluna >= this.matriz.length) {
                this.coluna = 0;
                this.linha++;

            }
            this.matriz[this.linha][this.coluna] = tokenASerSalvo;
            this.coluna++;
        }

    }

    @Override
    public Token getTokenNaPosicao(int linha, int coluna) {
        return this.matriz[coluna][linha];
    }

    @Override
    public int getLinhaAtual() {
        return this.linha;
    }

    @Override
    public int getColunaAtual() {
        return this.coluna;
    }

    @Override
    public int getLinhaMax() {
        return this.tamMaxL;
    }

    @Override
    public int getColunaMax() {
        return this.tamMaxC;
    }

}
