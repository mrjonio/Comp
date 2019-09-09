import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;


public class LexicalAnalyzerSimples {

    public static void main(String[] args) throws IOException {

        String rootPath = Paths.get("").toAbsolutePath(). toString();
        String subPath = "/src/";

        String sourceCode = rootPath + subPath + "/testes.txt";

        LexicalAnalyzer lexical = new LexicalAnalyzer(new FileReader(sourceCode));

        Token token;

        while ((token = lexical.yylex()) != null) {
            System.out.println("<" + token.name + ", " + token.value + "> (" + token.line + " - " + token.column + ")");
        }
    }
}