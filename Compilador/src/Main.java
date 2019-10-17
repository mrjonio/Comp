import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer(200, 200);
        String resourcePath = null;
        switch (System.getProperty("os.name")) {
            case "Linux":  resourcePath = "/home/antonio/Documentos/Comp/Compilador/src/teste.txt";
                break;
            case "Windows 10":  resourcePath = "C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\teste.txt";
                break;
        }
        lex.lerCodigoFonte(resourcePath);
        IMatrizDeSimbolos m = lex.getMatriz();

        try {
            Parser parser = new Parser(0, 0, m);
            for (int i =0; i < parser.getArvoreSintatica().size(); i++){
                System.out.print(parser.getArvoreSintatica().get(i) + " -> ");
            }
        } catch (SintaxError sintaxError) {
            sintaxError.mostrarErro();
        }



        //lex.lerCodigoFonte("/home/antonio/Documentos/Comp/Compilador/src/teste.txt");
    }
}
