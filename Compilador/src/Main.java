import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer();
        lex.lerCodigoFonte("C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\teste.txt");
        IMatrizDeSimbolos m = lex.getMatriz();
        for (int i = 0; i <= m.getLinhaAtual(); i++){
            for (int j = 0; j < m.getColunaAtual(); j++){
                System.out.println(m.getTokenNaPosicao(i, j).getLexema() + "" + m.getTokenNaPosicao(i, j).getValor() + ">");
            }
        }
        //lex.lerCodigoFonte("/home/antonio/Documentos/Comp/Compilador/src/teste.txt");
    }
}
