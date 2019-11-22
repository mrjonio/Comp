import java.util.ArrayList;

public class AritmeticOps {
    public ArrayList<String> expressaoCorreta;
    public ArrayList<Integer> oredem;
    public ArrayList<String> pais;
    public ArrayList<Integer> paisPrio;
    public ArrayList<String> temp;
    public int prioridade;
    public int prioridadeMax;

    public AritmeticOps(){
        this.expressaoCorreta = new ArrayList<>();
        this.oredem = new ArrayList<>();
        this.pais = new ArrayList<>();
        this.paisPrio = new ArrayList<>();
        this.temp = new ArrayList<>();
        temp.add("(");
        this.prioridade = 0;
        this.prioridadeMax = 0;
    }

    public void limpaTemp(){
        temp.clear();
        temp.add("(");
    }


}
