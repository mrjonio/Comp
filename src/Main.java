import jflex.*;
import jacc.*;
import java.io.File;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String rootPath = Paths.get("").toAbsolutePath(). toString();
        String subPath = "/src/";

        String file = rootPath + subPath + "testes.txt";
    }
}
