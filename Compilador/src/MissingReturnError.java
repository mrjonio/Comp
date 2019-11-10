public class MissingReturnError extends Exception {
    private String nomeFuncao;
    private String tipoEsperado;
    private String tipoRecebido;
    private int linha;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public MissingReturnError(String nomeFuncao, String tipoEsperado, String tipoRecebido, int linha) {
        this.nomeFuncao = nomeFuncao;
        this.tipoEsperado = tipoEsperado;
        this.tipoRecebido = tipoRecebido;
        this.linha = linha;
    }
    public MissingReturnError(String nomeFuncao, int linha) {
        this.nomeFuncao = nomeFuncao;
        this.linha = linha;
    }

    public void erroFaltaRetorno(){
        System.out.println("Retorno faltando para a função:" + nomeFuncao + " Em: " + linha);
    }

    public void retornoIncorreto(){
        System.out.println("Retorno incorreto da função: " + nomeFuncao + " esperado: " + tipoEsperado + " recebido: " + tipoRecebido);
    }

    public boolean retIncorreto(){
        if (tipoEsperado != null){
          return true;
        }
        return false;
    }
}
