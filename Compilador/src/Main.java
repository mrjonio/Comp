import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer(400, 400);
        String resourcePath = null;
        switch (System.getProperty("os.name")) {
            case "Linux":  resourcePath = "/home/antonio/Documentos/Comp/Compilador/src/teste.txt";
                break;
            case "Windows 10":  resourcePath = "C:\\Users\\carlo\\Documents\\Antônio-Adelino_Carlos-Antônio\\Comp\\Compilador\\src\\teste.txt";
                break;
        }
        lex.lerCodigoFonte(resourcePath);
        IMatrizDeSimbolos m = lex.getMatriz();

        try {
            Parser parser = new Parser(0, 0, m);
            for (int i =0; i < parser.getArvoreSintatica().size(); i++){
                System.out.print(parser.getArvoreSintatica().get(i) + " -> ");
            }
        } catch (SintaxError sintaxError) {
            sintaxError.mostrarErro();
        } catch (JaDeclaradoError jaDeclaradoError){
            jaDeclaradoError.mostrarErro();
        } catch (NaoDeclaradoError naoDeclaradoError){
            naoDeclaradoError.mostrarErro();
        } catch (EscopoInacessivelError escopoInacessivelError){
            escopoInacessivelError.mostrarErro();
        } catch (FuncaoNaoDeclaradaError funcaoNaoDeclaradaError) {
            funcaoNaoDeclaradaError.mostrarErro();
        } catch (TypeError typeError) {
            typeError.mostrarErro();
        } catch (OverflowParamsError overflowParamsError) {
            overflowParamsError.mostrarErro();
        } catch (UnderflowParamsError underflowParamsError) {
            underflowParamsError.mostrarErro();
        } catch (ParamRepeatError paramRepeatError) {
            paramRepeatError.mostrarErro();
        } catch (MissingReturnError missingReturnError) {
            if (missingReturnError.retIncorreto()) {
                missingReturnError.retornoIncorreto();
            } else {
                missingReturnError.erroFaltaRetorno();
            }
        } catch (RetornoIndesejadoError retornoIndesejadoError) {
            retornoIndesejadoError.mostrarErro();
        } catch (AritmeticError aritmeticError) {
            aritmeticError.exibirMensagemErro();
        } catch (DivisaoPorZeroError divisaoPorZeroError) {
            divisaoPorZeroError.mostrarErro();
        }


        //lex.lerCodigoFonte("/home/antonio/Documentos/Comp/Compilador/src/teste.txt");
    }
}
