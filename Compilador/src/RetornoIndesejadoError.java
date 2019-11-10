public class RetornoIndesejadoError extends Exception {
    private String nomeFuncao;

    public RetornoIndesejadoError(String nomeFuncao){
        this.nomeFuncao = nomeFuncao;
    }

    public void mostrarErro(){
        System.out.println("Função: " + nomeFuncao + " é do tipo VOID, não tem return");
    }
}
