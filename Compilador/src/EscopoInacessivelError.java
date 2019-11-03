public class EscopoInacessivelError extends Exception {
    private int linha;
    private String nome;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public EscopoInacessivelError(int linha, String nome) {
        this.linha = linha;
        this.nome = nome;
    }

    public void mostrarErro(){
        System.out.println("Escopo inacessível: " + nome + " em: " + linha);
    }
}

