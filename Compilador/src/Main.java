import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer(100, 100);
        lex.lerCodigoFonte("C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\teste.txt");
        IMatrizDeSimbolos m = lex.getMatriz();

        Parser parser = new Parser(0, 0, m);

        //lex.lerCodigoFonte("/home/antonio/Documentos/Comp/Compilador/src/teste.txt");
    }
}
