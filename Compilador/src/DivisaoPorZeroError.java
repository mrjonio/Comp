public class DivisaoPorZeroError extends Exception {
    int linha;

    public DivisaoPorZeroError(int linha){
        this.linha = linha;
    }

    public void mostrarErro(){
        System.out.println("Divisão por zero na linha: " + linha);
    }
}
