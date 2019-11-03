import java.util.ArrayList;

public class Escopo {

    private int id;
    private ArrayList<Integer> escoposPai;
    private int escopoMaximo;

    public Escopo(int id, ArrayList<Integer> array) {
        this.id = id;
        this.escoposPai = array;
    }
    public Escopo() {
        this.id = 0;
        this.escoposPai = new ArrayList<>();
        this.escoposPai.add(0);
    }

    public void incrementar(){
        this.id = ++this.escopoMaximo;
        int temp = this.id;
        if (this.escoposPai.contains(temp)) {
            return;
        }
        this.escoposPai.add(temp);
    }

    public void decrementar(){
        this.escoposPai.remove(this.escoposPai.size() - 1);
        this.id = this.escoposPai.get(this.escoposPai.size() - 1);
    }
    public void addEscopo(int escopo){
        this.id =  escopo;
    }

    public int getId() {
        return this.id;
    }
    public ArrayList<Integer> getEscoposPai() {
        return this.escoposPai;
    }
    public int getEscopoMaximo() {
        return this.escopoMaximo;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setEscoposPai( ArrayList<Integer> pais) {
        this.escoposPai = pais;
    }
    public void setEscopoMaximo(int max) {
        this.escopoMaximo = max;
    }
}
