import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {
    private IMatrizDeSimbolos matriz;
    private int line;
    private int tamAtual;
    private ArrayList tokenAtual;

    public Lexer(int tamLinha, int tamColuna) {
        this.line = 0;
        this.tamAtual = 0;
        this.tokenAtual = new ArrayList();
        this.matriz = new MatrizDeSimbolos(tamLinha, tamColuna);

    }

    public IMatrizDeSimbolos getMatriz() {
        return matriz;
    }

    void lerCodigoFonte(String caminho) throws IOException {
        BufferedReader sc = new BufferedReader(new FileReader(caminho));
        while (sc.ready()){
            String linha = sc.readLine();
            char[] linhaChar = linha.toCharArray();
            for (int i = 0; i < linhaChar.length; i++){
                if (linhaChar[i] != ' '){
                    lerToken(Arrays.copyOfRange(linhaChar, i, (linhaChar.length)));
                    break;
                }
            }
            this.line++;
        } Token tokenFinal = new Token("final", "< $, ", this.line,
                "$", this.matriz.getLinhaAtual(), this.matriz.getColunaAtual());
        this.matriz.alocarToken(tokenFinal);
    }

    private void lerToken(char[] linha){
        for (char aLinha : linha)
            if (aLinha == '#') {
                break;
            } else {
                if ((aLinha == ' ') || (aLinha == '(') || (aLinha == ')')
                        || (aLinha == ';') || (aLinha == ',') || (aLinha == ':') || aLinha == '\n' || aLinha == '.') {
                    char[] tokenPego = new char[this.tamAtual];
                    for (int j = 0; j < this.tamAtual; j++) {
                        tokenPego[j] = this.tokenAtual.get(j).toString().charAt(0);
                    }
                    String tokenPegoString = String.copyValueOf(tokenPego);
                    if (this.tamAtual > 0) {
                        String tipo = identificarLexema(tokenPegoString);
                        Token tokenFinal = new Token(tokenPegoString, tipo, this.line, tokenPegoString, this.matriz.getLinhaAtual(),
                                this.matriz.getColunaAtual());
                        this.matriz.alocarToken(tokenFinal);
                        this.tokenAtual.clear();
                        this.tamAtual = 0;
                    }
                    if (aLinha != ' ') {
                        Token tokenExcept = new Token((Character.toString(aLinha)), "<" + Character.toString(aLinha) + ", ",
                                this.line, Character.toString(aLinha), this.matriz.getLinhaAtual(), this.matriz.getColunaAtual());
                        this.matriz.alocarToken(tokenExcept);
                    }
                } else {
                    this.tokenAtual.add(aLinha);
                    this.tamAtual++;
                }
            }
    }

    private String identificarLexema(String tokenAtualIdentificado){
        switch (tokenAtualIdentificado){
            case "if":
                return "if";
            case "else":
                return "else";
            case "while":
                return "while";
            case "break":
                return "break";
            case "continue":
                return "continue";
            case "program":
                return "program";
            case "var":
                return "var";
            case "procedure":
                return "procedure";
            case "begin":
                return "begin";
            case "return":
                return "return";
            case "end":
                return "end";
            case "write":
                return "write";
            case "do":
                return "do";
            case "then":
                return "then";
            case "not":
                return "not";
            case "+":
                return "+_operator";
            case "-":
                return "-_operator";
            case "or":
                return "or_operator";
            case "*":
                return "*_operator";
            case "and":
                return "and_operator";
            case "div":
                return "div_operator";
            case "=":
                return "=_operator";
            case "<>":
                return "<>_operator";
            case "<":
                return "<_operator";
            case "<=":
                return "<=_operator";
            case ">=":
                return "<>=_operator";
            case ">":
                return ">_operator";
            case ":":
                return ":_operator";
            case ".":
                return "._final";
            case "void":
                return "Void";
            default:
                if (tokenAtualIdentificado.charAt(0) == '0' || tokenAtualIdentificado.charAt(0) == '1' || tokenAtualIdentificado.charAt(0) == '2' ||
                        tokenAtualIdentificado.charAt(0) == '3' || tokenAtualIdentificado.charAt(0) == '4' || tokenAtualIdentificado.charAt(0) == '5' ||
                        tokenAtualIdentificado.charAt(0) == '6' || tokenAtualIdentificado.charAt(0) == '7' || tokenAtualIdentificado.charAt(0) == '8' ||
                        tokenAtualIdentificado.charAt(0) == '9'){
                    return "integer_constant";
                }else {
                    return "identifier";
                }

        }

    }
}
