public class ParamRepeatError extends Exception{
    private String nomeFuncao;
    private String nome;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link }.
     */
    public ParamRepeatError(String linha, String nome) {
        this.nomeFuncao = linha;
        this.nome = nome;
    }

    public void mostrarErro() {
        System.out.println("Parametro repetido: " + nome + " em: " + nomeFuncao);
    }
}
