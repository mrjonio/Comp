import jflex.*;

import java.io.File;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String rootPath = Paths.get("").toAbsolutePath(). toString();
        String subPath = "/src/";

        String file = rootPath + subPath + "linguagemSimples.lex";

        File sourceCode = new File(file);

        jflex.Main.generate(sourceCode);
    }
}
