import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ContadoresTraducao {
    int qtdIf;
    int qtdElse;
    int qtdWhile;
    int funcAtual;
    int contGeral;
    int statAtual;
    int temElse;
    boolean ifStatement;
    boolean whileStatement;
    String labelIf;
    String labelWhile;
    String labelFunc;
    String labelGeral;
    String stat;
    ArrayList<String> expressaoCrua;
    ArrayList<String> traduzida;
    ArrayList<String> ifLabels;
    ArrayList<String> elsesLabels;
    ArrayList<String> iffinais;
    ArrayList<String> elsefinais;
    ArrayList<String> whileFinais;
    ArrayList<String> gotoGeral;
    ArrayList<String> funcRet;
    String varatt;
    String funcatt;
    int gen = 0;
    File saida = new File("C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\saida.txt");
    FileWriter ntemnomemais = new FileWriter(saida, true);
    BufferedWriter out;

    public  ContadoresTraducao() throws IOException {
        qtdIf = 0;
        qtdElse = 0;
        qtdWhile = 0;
        funcAtual = 0;
        statAtual = 0;
        labelIf = "_if";
        labelFunc = "_func";
        labelGeral = "_generic";
        labelWhile = "_while";
        stat = "_stat";
        contGeral = 0;
        this.expressaoCrua = new ArrayList<>();
        this.traduzida = new ArrayList<>();
        ifStatement = false;
        whileStatement = false;
        ifLabels = new ArrayList<>();
        elsesLabels = new ArrayList<>();
        out = new BufferedWriter(ntemnomemais);
        iffinais = new ArrayList<>();
        elsefinais = new ArrayList<>();
        whileFinais = new ArrayList<>();
        gotoGeral = new ArrayList<>();
        funcRet = new ArrayList<>();
        temElse = 0;
        varatt = null;
        funcatt = null;
    }
}
