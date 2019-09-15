import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer();
        //lex.lerCodigoFonte("C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\teste.txt");
        lex.lerCodigoFonte("/home/antonio/Documentos/Comp/Compilador/src/teste.txt");
        ArrayList<Token> a = lex.getTokens();
        for (int i = 0; i < a.size(); i++){
            System.out.println(a.get(i).getLexema() + "" + a.get(i).getValor() + ">");
        }
    }
}
