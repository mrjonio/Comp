import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.ArrayList;

public class MatrizDeSimbolos implements IMatrizDeSimbolos{
    private int linha;
    private int coluna;
    private Token[][] matriz;
    private int tamMaxL;
    private int tamMaxC;
    ArrayList<Token> funcoes;
    ArrayList<Token> variaveis;

    public MatrizDeSimbolos(int tamL, int tamC) {
        this.linha = 0;
        this.coluna = 0;
        this.matriz = new Token[tamL][tamC];
        this.funcoes = new ArrayList<>();
        this.variaveis =  new ArrayList<>();
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
    public void addFuncao(Token funcao) throws JaDeclaradoError {
        if (!this.funcoes.contains(funcao))
            this.funcoes.add(funcao);
        else
            throw new JaDeclaradoError(funcao.getLinha(), funcao.getNome());
    }

    @Override
    public Token buscarVariavel(String nomeDaVariavel){
        for (Token a: variaveis) {
            if (a.getNome().toCharArray().length == 0) {
                if (a.getNome() == nomeDaVariavel){
                    return a;
                }
            } else {
                if (a.getNome().equals(nomeDaVariavel)){
                    return a;
                }
            }
        }
        return null;
    }

    @Override
    public Token buscarFuncao(String nomeDaFuncao){
        for (Token a : funcoes){
            if (a.getNome().equals(nomeDaFuncao)){
                return a;
            }
        }
        return null;
    }


    @Override
    public void addVariavel(Token variavel) throws JaDeclaradoError {
            this.variaveis.add(variavel);
    }

    @Override
    public ArrayList<Token> funcs() {
        return this.funcoes;
    }

    @Override
    public ArrayList<Token> vars() {
        return this.variaveis;
    }


    @Override
    public Token buscarToken(int linha, int coluna){
        Token t = this.matriz[linha][coluna];
        if (linha <= this.linha){
            for (int i = 0; i <= linha; i++) {
                if (i != linha) {
                    for (int j = 0; j <= tamMaxC; j++) {
                        if (t.getValor().equals(matriz[i][j].getValor())) {
                            return matriz[i][j];
                        }
                    }
                } else {
                    for (int j = 0; j <= coluna - 1; j++){
                        if (t.getValor().equals(matriz[i][j].getValor())) {
                            return matriz[i][j];
                        }
                    }
                }
            }
        }
        return null;
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

    @Override
    public int getLinhaMax() {
        return this.tamMaxL;
    }

    @Override
    public int getColunaMax() {
        return this.tamMaxC;
    }

}
