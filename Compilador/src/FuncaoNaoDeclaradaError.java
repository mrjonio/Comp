public class FuncaoNaoDeclaradaError extends Exception{
    private int linha;
    private String nome;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link }.
     */
    public FuncaoNaoDeclaradaError(int linha, String nome) {
        this.linha = linha;
        this.nome = nome;
    }

    public void mostrarErro() {
        System.out.println("Função não declarada: " + nome + " em: " + linha);
    }
}

