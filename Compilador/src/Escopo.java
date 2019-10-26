import java.util.ArrayList;

public class Escopo {

    private int id;
    private ArrayList<Integer> escoposPai;
    private int escopoMaximo;

    public Escopo() {
        this.escoposPai = new ArrayList<Integer>();
    }

    public void incrementar(){
        this.escoposPai.add(this.id);
        this.id = this.escopoMaximo + 1;
        this.escopoMaximo = this.escopoMaximo + 1;
    }

    public void decrementar(){

        int tamanho = this.escoposPai.size() - 1;
        this.escoposPai.remove(tamanho);
        this.id = this.id - 1;
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
