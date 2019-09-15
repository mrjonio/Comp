import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {
    private ArrayList tokens;
    private int line;
    private int tamAtual;
    private ArrayList tokenAtual;

    public Lexer() {
        this.tokens = new ArrayList<Token>();
        this.line = 0;
        this.tamAtual = 0;
        this.tokenAtual = new ArrayList();
    }

    public void leCodigoFonte(String caminho) throws IOException {
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
        }
    }

    private void lerToken(char[] linha){
        this.line++;
        for (char aLinha : linha) {
            if (aLinha == '#'){
                break;
            } else{
            if ((aLinha == ' ') || (aLinha == '[') || (aLinha == ']') || (aLinha == '>') || (aLinha == '(') || (aLinha == ')') || (aLinha == '{') || (aLinha == '}')
                    || (aLinha == '=') || (aLinha == ';') || (aLinha == '<') || (aLinha == '+') || (aLinha == '*') || (aLinha == '-') || (aLinha == ',') || aLinha == '\n') {
                    char[] tokenPego = new char[tamAtual];
                    for (int j = 0; j < tamAtual; j++) {
                        tokenPego[j] = tokenAtual.get(j).toString().charAt(0);
                    }
                    String tokenPegoString = String.copyValueOf(tokenPego);
                    Token tokenExcept = new Token((Character.toString(aLinha)), "<" + Character.toString(aLinha) + ", ", this.line, Character.toString(aLinha));
                    if (tamAtual > 0) {
                        String tipo = identificaLexema(tokenPegoString);
                        Token tokenFinal = new Token(tokenPegoString, tipo, this.line, tokenPegoString);
                        tokens.add(tokenFinal);
                        tokenAtual.clear();
                        tamAtual = 0;
                    }
                    if (aLinha != ' ') {
                        tokens.add(tokenExcept);
                    }
            } else {
                if(aLinha != '\n' && aLinha != ' ') {
                    this.tokenAtual.add(aLinha);
                    this.tamAtual++;
                }
            }
            }
        }
    }

    private String identificaLexema(String tokenAtualIdentificado){
        switch (tokenAtualIdentificado){
            case "if":
                return "<if, ";
            case "else":
                return "<else, ";
            case "while":
                return "<while, ";
            case "break":
                return "<break, ";
            case "continue":
                return "<continue, ";
            case "program":
                return "<program, ";
            case "var":
                return "<var, ";
            case "procedure":
                return "<procedure, ";
            case "begin":
                return "<begin, ";
            case "return":
                return "<return, ";
            case "end":
                return "<end, ";
            case "write":
                return "<write, ";
            case "do":
                return "<do, ";
            case "then":
                return "<then, ";
            case "not":
                return "<not, ";
            case "and":
                return "<and, ";
            case "or":
                return "<or, ";
            case "div":
                return "<div, ";
                default:
                    if (tokenAtualIdentificado.charAt(0) == '0' || tokenAtualIdentificado.charAt(0) == '1' || tokenAtualIdentificado.charAt(0) == '2' ||
                            tokenAtualIdentificado.charAt(0) == '3' || tokenAtualIdentificado.charAt(0) == '4' || tokenAtualIdentificado.charAt(0) == '5' ||
                            tokenAtualIdentificado.charAt(0) == '6' || tokenAtualIdentificado.charAt(0) == '7' || tokenAtualIdentificado.charAt(0) == '8' ||
                    tokenAtualIdentificado.charAt(0) == '9'){
                        return "<integer constant, ";
                    }else {
                        return "<identifier, ";
                    }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
