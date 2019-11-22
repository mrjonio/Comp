public class AritmeticError extends Exception{
    private String nome;
    private int linha;

    public AritmeticError(String nome, int linha){
        this.nome = nome;
        this.linha = linha;
    }

    public void exibirMensagemErro(){
        System.out.println("Operando/operador incorreto em : " + nome +  " na linha:" + linha);
    }

}
