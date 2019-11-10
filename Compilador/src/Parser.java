import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.ToLongBiFunction;


public class Parser {
    private int linhaAtual;
    private int colunaAtual;
    private Stack<String> pilha;
    private IMatrizDeSimbolos matrizDeSimbolos;
    private ArrayList<String> arvore;
    private Escopo escopoGeral;
    private boolean isDeclaracao;
    private boolean isVariavel;
    private boolean isPrograma;
    private ArrayList<Integer> escopoTemp;
    private boolean isFuncao;
    private ArrayList<String> paramsFunc;
    private Token funcaoAtual;
    private String tipoAtual;
    private Token variavelAtual;
    private boolean isChamada;
    private boolean analiseParams;
    private int qtdparams;
    private Token funcaoAtualChamada;
    private boolean isExpressao;
    private ArrayList<Token> expressao;
    private ArrayList<String> paramsNames;
    private boolean analiseRep;
    private Token funcaoVarsEscopo;
    private ArrayList<String> paramsEscopoFuncao;
    private boolean analiseReturn;
    private boolean isLogica;
    private ArrayList<Token> logica;

    public Parser(int linhaAtual, int colunaAtual, IMatrizDeSimbolos matrizDeSimbolos) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError {
        this.linhaAtual = linhaAtual;
        this.colunaAtual = colunaAtual;
        this.pilha = new Stack<String>();
        this.matrizDeSimbolos = matrizDeSimbolos;
        this.arvore = new ArrayList<>();
        this.escopoGeral = new Escopo();
        this.isDeclaracao = false;
        this.isVariavel = false;
        this.isPrograma = false;
        this.escopoTemp = null;
        this.funcaoAtual = null;
        this.isFuncao = false;
        this.paramsFunc = new ArrayList<>();
        this.tipoAtual = null;
        this.variavelAtual = null;
        this.isChamada = false;
        this.analiseParams = false;
        this.qtdparams = 0;
        this.funcaoAtualChamada = null;
        this.isExpressao = false;
        this.expressao = new ArrayList<>();
        this.paramsNames = new ArrayList<>();
        this.analiseRep = false;
        this.funcaoVarsEscopo = null;
        this.paramsEscopoFuncao = new ArrayList<>();
        this.analiseReturn = false;
        this.isLogica = false;
        this.logica = new ArrayList<>();
        iniciar();
    }

    private void iniciar() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError {
        pilha.push("$");
        pilha.push("<program>");
        String regra;
        regra = pilha.pop();
        analisaRegra(regra);
        analiseSintatica();
    }

    public ArrayList getArvoreSintatica(){
        return this.arvore;
    }


    private void analisaRegra(String regraAtual) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError {
        arvore.add(regraAtual);
        Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
        //System.out.println( regraAtual + " " + pilha.peek() + " " + this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor());
        switch (regraAtual) {
            case "<program>":
                pilha.push(".");
                pilha.push("<block>");
                pilha.push(":");
                pilha.push("<identifier>");
                pilha.push("program");
                this.isPrograma = true;
                break;
            case "program":
                if (lookAhead("program")) {
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<identifier>":
                if (this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().length() > 1) {
                    pilha.push("<letter_or_digit>");
                }
                if (this.analiseRep){
                    analiseRepeat(a);
                }

                if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) && !this.isFuncao){
                    if (a.getLexema().equals("identifier") && !(isSpecialSymbol(a.getNome())) && !this.isChamada) {
                        if (validarEscopo(a)) {
                            Escopo setar = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                            a.setEscopo(setar);
                        }
                    }
                    if (this.isFuncao && !this.isChamada) {
                        if (this.matrizDeSimbolos.buscarFuncao(a.getNome()) != null){
                            throw new JaDeclaradoError(this.matrizDeSimbolos.buscarFuncao(a.getNome()).getLinha(),
                                    a.getNome());
                        }
                        if (this.paramsFunc.size() == 1) {
                            Escopo temp = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                            a.setEscopo(temp);
                            this.matrizDeSimbolos.addFuncao(a);
                            this.funcaoAtual = a;
                        }
                    }

                    if (isChamada){
                        if (analisaEscopoFuncao(a)){
                            analiseParams = true;
                            this.paramsFunc.clear();
                            break;
                        }
                    }
                    if (analiseParams){
                        if (isALetter(a.getNome().charAt(0)) || isADigit(a.getNome().charAt(0))) {
                            if (analisaParam(a)) {
                                if (this.funcaoAtualChamada.getParametros().size() > this.qtdparams) {
                                    //System.out.println(matrizDeSimbolos.buscarVariavel(a.getNome()).getRetornoFuncao());
                                    if (matrizDeSimbolos.buscarVariavel
                                            (a.getNome()).getRetornoFuncao().equals(this.funcaoAtualChamada.getParametros().get(this.qtdparams))) {
                                        qtdparams++;
                                        analiseParams = true;
                                    } else {
                                        throw new TypeError(a.getLinha(), a.getNome());
                                    }
                                } else {
                                    throw new OverflowParamsError(a.getLinha(), a.getNome());
                                }
                            }
                        } else {
                            throw new TypeError(a.getLinha(), a.getNome());
                        }
                    }
                }
                pilha.push("<letter>");
                break;
            case "<variable_att>":
                if (!isPalavraReservada(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                        isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0))){
                    pilha.push("<block>");
                    pilha.push("<att_choose>");
                    pilha.push("=");
                    pilha.push("<identifier>");
                }
                break;
            case "<att_choose>":
                if (lookAhead("call")){
                    pilha.push("<procedure_statement>");
                } else {
                    pilha.push(";");
                    pilha.push("<identifier_or_value>");
                }
                break;
            case "<letter>":
                if (!isPalavraReservada(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor())) {
                    if (isALetter(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0))) {
                        if (matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().substring(1).length() <= 0) {
                            incrementaPosToken();
                        }
                    } else {
                        throw new SintaxError(a.getLinha(), a.getValor());
                    }
                }
                break;
            case "<letter_or_digit>":
                boolean flag = true;
                if (!isPalavraReservada(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor())) {
                    for (int i = 0; i < matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().length(); i++) {
                        if (!(isALetter(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(i)))
                                && !(isADigit(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(i)))) {
                            flag = false;
                            break;
                        }
                    } if(flag) {
                        Token b = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                        if (b.getLexema().equals("identifier") && (!isSpecialSymbol(b.getNome())) && !isFuncao && !isChamada) {
                            if (validarEscopo(b)) {
                                Escopo setar = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                                b.setEscopo(setar);
                            }
                        }
                        if (this.isFuncao && !this.isChamada) {
                            this.analiseRep = true;
                            if (this.matrizDeSimbolos.buscarFuncao(a.getNome()) != null){
                                throw new JaDeclaradoError(this.matrizDeSimbolos.buscarFuncao(a.getNome()).getLinha(),
                                        a.getNome());
                            }
                            if (this.paramsFunc.size() == 1) {
                                Escopo temp = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                                a.setEscopo(temp);
                                this.matrizDeSimbolos.addFuncao(a);
                                this.funcaoAtual = a;
                            }
                        }
                        incrementaPosToken();
                    } else {
                        //Else: empty
                    }
                }
                break;
            case ";":
                if (lookAhead(";")) {
                    if (isFuncao){
                        Token func = this.matrizDeSimbolos.buscarFuncao(this.funcaoAtual.getNome());
                        func.setRetornoFuncao(this.paramsFunc.get(0));
                        this.paramsFunc.remove(0);
                        ArrayList<String> paramTemp = (ArrayList<String>) this.paramsFunc.clone();
                        func.setParametros(paramTemp);
                        this.paramsFunc.clear();
                    }
                    if (isDeclaracao){
                        String tipo = this.tipoAtual;
                        this.variavelAtual.setRetornoFuncao(tipo);
                    }
                    if (isExpressao){
                        if (this.expressao.size() > 2){
                            if (this.expressao.size() == 4){

                            } else {

                            }

                        }
                    }
                    this.isExpressao = false;
                    this.isFuncao = false;
                    this.isDeclaracao = false;
                    this.isPrograma = false;
                    this.qtdparams = 0;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<block>":
                pilha.push("<statement>");
                pilha.push("<statement_part>");
                pilha.push("<procedure_declaration_part>");
                pilha.push("<variable_declaration_part>");
                break;
            case "<statement_part>":
                if (lookAhead("begin")) {
                    pilha.push("<block>");
                    pilha.push("<compound_statement>");
                } //Else: <Empty>
                break;
            case "<compound_statement>":
                pilha.push(";");
                pilha.push("end");
                pilha.push("<optional_compound>");
                pilha.push("<compound_block>");
                pilha.push("<statement>");
                pilha.push(":");
                pilha.push("begin");
                break;
            case "begin":
                if (lookAhead("begin")) {
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<statement>":
                if (lookAhead("begin") || lookAhead("while") || lookAhead("if")) {
                    pilha.push("<block>");
                    pilha.push("<structured_statement>");
                } else if (lookAhead("write") || lookAhead("call")  || lookAhead("procedure") ||
                lookAhead("Integer") || lookAhead("Boolean") ||
                        (!isPalavraReservada(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                                isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)))){
                            pilha.push("<block>");
                            pilha.push("<simple_statement>");
                } //Else: empty
                break;
            case "<compound_block>":
                if (lookAhead(";")) {
                    pilha.push("<statement>");
                    incrementaPosToken();
                } //Else: <empty>, ou seja, nada rs
                break;
            case "<optional_compound>":
                if (lookAhead("return")) {
                    pilha.push(";");
                    pilha.push("<identifier>");
                    pilha.push("return");
                } //Else: <empty>, só retira do topo da pilha mesmo
                break;
            case "end":
                if (lookAhead("end")) {
                    if (this.funcaoVarsEscopo != null){
                        if (!this.funcaoVarsEscopo.getRetornoFuncao().equals("Void")){
                            if (this.analiseReturn){
                                this.analiseReturn = false;
                            } else {
                                throw new MissingReturnError(this.funcaoVarsEscopo.getNome(), a.getLinha() - 1);
                            }

                        } else if (this.analiseReturn){
                            throw new RetornoIndesejadoError(this.funcaoVarsEscopo.getNome());
                        }
                    }
                    this.funcaoVarsEscopo = null;
                    this.paramsEscopoFuncao.clear();
                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<variable_declaration_part>":
                if (lookAhead("Integer") || lookAhead("Boolean")) {
                        this.isDeclaracao = true;
                        pilha.push("<variable_declaration_part_optional>");
                        pilha.push("<identifier>");
                        pilha.push("<predefined_identifier>");
                    // TEM QUE ADICIONAR O ESCOPO + VALOR SEMÂNTICO!!!!!!
                } //Else: <empty>
                break;
            case "<variable_declaration_part_optional>":
                if (lookAhead("=")){
                    pilha.push("<block>");
                    pilha.push(";");
                    pilha.push("<identifier_or_value>");
                    pilha.push("=");
                } else if (lookAhead(";")){
                    pilha.push("<block>");
                    pilha.push(";");
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<procedure_declaration_part>":
                if (lookAhead("procedure")) {
                    pilha.push("<procedure_end>");
                    pilha.push(")");
                    pilha.push("<parameters>");
                    pilha.push("(");
                    pilha.push("<identifier>");
                    pilha.push("<predefined_identifier_procedure>");
                    pilha.push("procedure");
                } //Else: <empty>
                break;
            case "<procedure_declaration>:":
                pilha.push("<block>");
                break;
            case "<predefined_identifier_procedure>":
                if (lookAhead("Integer")) {
                    pilha.push("Integer");
                }
                else if (lookAhead("Boolean")) {
                    pilha.push("Boolean");
                } else if (lookAhead("Void")) {
                    pilha.push("Void");
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<procedure_end>":
                if (lookAhead(";")){
                    pilha.push("<block>");
                    pilha.push(";");
                } if (lookAhead(":")){
                    pilha.push("<procedure_final>");
                    pilha.push("<procedure_declaration>");
                    pilha.push(":");
            }
            break;
            case "<procedure_final>":
                if (lookAhead("end")){
                    pilha.push("<block>");
                    pilha.push(";");
                    pilha.push("end");
                } else {
                    pilha.push("<block>");
                }
                break;
            case "procedure":
                if (lookAhead("procedure")) {
                    this.funcaoVarsEscopo = null;
                    this.paramsEscopoFuncao.clear();
                    this.isFuncao = true;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }

                break;
            case "<simple_statement>":
                if (lookAhead("write")) {
                    pilha.push("<write_statement>");
                } else {
                    if (lookAhead("call")) {
                        pilha.push("<procedure_statement>");
                    } else if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                            isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0))){
                        pilha.push("<variable_att>");
                    } else if(lookAhead("Integer") || lookAhead("Boolean")){
                        pilha.push("<variable_declaration_part>");
                    }
                }
                break;
            case "<parameters>":
                if (lookAhead("Integer") || lookAhead("Boolean")) {
                    pilha.push("<parameter>");
                    pilha.push("<variable_parameter>");
                } //Else: empty
                break;
            case "<parameter>":
                if (lookAhead(",")) {
                    pilha.push("<parameter>");
                    pilha.push("<variable_parameter>");
                    pilha.push(",");
                } //Else: <Empty>
                break;
            case "<call_parameters>":
                this.pilha.push("<param>");
                this.pilha.push("<call_param>");
                break;
            case "<param>":
                if (lookAhead(",")) {
                    this.pilha.push("<param>");
                    this.pilha.push("<call_param>");
                    this.pilha.push(",");
                }
                break;
            case "<call_param>":
                if (!isSpecialSymbol(a.getNome())){
                    pilha.push("<identifier>");
                }
                break;
            case "<detour_statement>":
                if (lookAhead("break")) {
                    pilha.push(";");
                    pilha.push("break");
                } else if (lookAhead("continue")){
                    pilha.push(";");
                    pilha.push("continue");
                } //Else: empty
                break;
            case "<procedure_statement>":
                pilha.push(";");
                pilha.push("<procedure_identifier>");
                pilha.push("call");
                break;
            case "<procedure_identifier>":
                pilha.push(")");
                pilha.push("<call_parameters>");
                pilha.push("(");
                pilha.push("<identifier>");
                break;
            case "<write_statement>":
                pilha.push(";");
                pilha.push(")");
                pilha.push("<write_params>");
                pilha.push("(");
                pilha.push("write");
                break;
            case "<write_params>":
                if (lookAhead("call")){
                    pilha.push("<procedure_statement>");
                } else if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                        (isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)) ||
                                isADigit(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)))){
                    pilha.push("<identifier_or_value>");
                }
                break;
            case "<structured_statement>":
                if (lookAhead("begin")) {
                    pilha.push("<compound_statement>");
                } else {
                    if (lookAhead("if")) {
                        pilha.push("<if_statement>");
                    } else {
                        pilha.push("<while_statement>");
                    }
                }
                break;
            case "<if_statement>":
                pilha.push(";");
                pilha.push("endif");
                pilha.push("<else_statement>");
                pilha.push("<detour_statement>");
                pilha.push("<statement>");
                pilha.push(":");
                pilha.push("then");
                pilha.push("<expression>");
                pilha.push("if");
                break;
            case "<else_statement>":
                if (lookAhead("else")) {
                    pilha.push(";");
                    pilha.push("endelse");
                    pilha.push("<detour_statement>");
                    pilha.push("<statement>");
                    pilha.push(":");
                    pilha.push("else");
                } //Else: <empty>
                break;
            case "<while_statement>":
                pilha.push(";");
                pilha.push("endwhile");
                pilha.push("<detour_statement>");
                pilha.push("<statement>");
                pilha.push(":");
                pilha.push("do");
                pilha.push("<expression>");
                pilha.push("while");
                break;

            //Antônio//
            case "<expression>":

                if (lookAhead("true") || lookAhead("false")){
                    pilha.push("<boolean_value>");
                }else{                
                    pilha.push("<after_expression>");  
                    pilha.push("<complement_expression>");
                    pilha.push("<simple_expression>");
                }
                break;
            case "<after_expression>":
                if (lookAhead("and")){
                    pilha.push("<expression>");
                    pilha.push("and");
                } else if (lookAhead("or")){
                    pilha.push("<expression>");
                    pilha.push("or");
                } //Else: empty
                break;
            case "<complement_expression>":
                if (lookAhead("=") || lookAhead("<>") || lookAhead("<") || lookAhead("<=") ||
                lookAhead(">=") || lookAhead(">")){
                    pilha.push("<expression>");
                    pilha.push("<relational_operator>");
                } // Else: <empty>
                 break;//
            case "<simple_expression>":
                pilha.push("<simple_expression_complement>");
                pilha.push("<factor>");
                break;
            case "<simple_expression_complement>":
                if (lookAhead("+") || lookAhead("-") || lookAhead("or")){
                    pilha.push("<adding_operator1>");
                } else if (lookAhead("*") || lookAhead("div") || lookAhead("and")){
                    pilha.push("<multiplying_operator1>");
                } //Else: empty
                break;
            case "<adding_operator1>":
                if (lookAhead("+") || lookAhead("-") || lookAhead("or")) {
                    pilha.push("<expression>");
                    pilha.push("<adding_operator>");
                } else{
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<multiplying_operator1>":
                if (lookAhead("*") || lookAhead("div") || lookAhead("and")) {
                    pilha.push("<expression>");
                    pilha.push("<multiplying_operator>");
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<factor>":
                this.expressao.add(a);
                if (lookAhead("(")) {
                    pilha.push(")");
                    pilha.push("<expression>");
                    pilha.push("(");
                } else if (lookAhead("not")) {
                    pilha.push("<factor>");
                    pilha.push("not");
                } else if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                        (isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)) ||
                                isADigit(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)) ||
                                        lookAhead("true") || lookAhead("false"))){
                    pilha.push("<identifier_or_value>");
                } //Else: empty
                break;
            case "<relational_operator>":
                if (lookAhead("=")) {
                    pilha.push("=");
                }
                if (lookAhead("<>")) {
                    pilha.push("<>");
                }
                if (lookAhead("<")) {
                    pilha.push("<");
                }
                if (lookAhead(">")) {
                    pilha.push(">");
                }
                if (lookAhead("<=")) {
                    pilha.push("<=");
                }
                if (lookAhead(">=")) {
                    pilha.push(">=");
                } //Else: empty
                break;
            case "<adding_operator>":
                if (lookAhead("+")) {
                    pilha.push("+");
                }
                if (lookAhead("-")) {
                    pilha.push("-");
                }
                if (lookAhead("or")) {
                    pilha.push("or");
                } //Else: empty
                break;
            case "<multiplying_operator>":
                if (lookAhead("*")) {
                    pilha.push("*");
                }
                if (lookAhead("div")) {
                    pilha.push("div");
                }
                if (lookAhead("and")) {
                    pilha.push("and");
                } //Else: empty
                break;
            case "<predefined_identifier>":
                if (lookAhead("Integer")) {
                    pilha.push("Integer");
                }
                else if (lookAhead("Boolean")) {
                    pilha.push("Boolean");
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<identifier_or_value>":
                this.isExpressao = true;
                if (lookAhead("true") || lookAhead("false")){
                    pilha.push("<boolean_value>");
                } else{
                    pilha.push("<simple_expression>");
                    pilha.push("<letter_or_digit>");
                }
                break;
            case "<compl_indv>":
                if (lookAhead("+") || lookAhead("or")){
                    pilha.push("<identifier_or_value>");
                    pilha.push("<adding_operator>");
                } else if(lookAhead("*") || lookAhead("and")){
                    pilha.push("<identifier_or_value>");
                    pilha.push("<multiplying_operator>");
                } //Else: empty
                break;
            case "<boolean_value>":
                if (lookAhead("true")){
                    pilha.push("true");
                } else if(lookAhead("false")){
                    pilha.push("false");
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOO");
                }
                break;

            case "(":
                if (lookAhead("(")){
                    this.isChamada = false;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ")":
                if (lookAhead(")")){
                    if (analiseParams){
                        if (this.qtdparams < this.funcaoAtualChamada.getParametros().size()){
                            throw new UnderflowParamsError(a.getLinha(), a.getNome());
                        }
                    }
                    this.analiseRep = false;
                    this.paramsNames.clear();
                    this.funcaoAtualChamada = null;
                    this.analiseParams = false;
                    this.isChamada = false;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "+":
                if (lookAhead("+")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "-":
                if (lookAhead("-")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "=":
                if (lookAhead("=")){
                    if (isDeclaracao){
                        String tipo = this.tipoAtual;
                        this.variavelAtual.setRetornoFuncao(tipo);
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "*":
                if (lookAhead("*")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "div":
                if (lookAhead("div")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "or":
                if (lookAhead("or")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "and":
                if (lookAhead("and")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">":
                if (lookAhead(">")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">=":
                if (lookAhead(">=")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<=":
                if (lookAhead("<=")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<>":
                if (lookAhead("<>")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<":
                if (lookAhead("<")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Void":
                if (lookAhead("Void")){
                    this.paramsFunc.add(a.getNome());
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "\"":
                if (lookAhead("\"")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ":":
                if (lookAhead(":")){
                    if (isFuncao){
                        this.funcaoAtual.setDeclarada(true);
                        ArrayList<String> tempPara = (ArrayList<String>) this.paramsFunc.clone();
                        this.funcaoAtual.setRetornoFuncao(tempPara.get(0));
                        tempPara.remove(0);
                        this.funcaoAtual.setParametros(tempPara);
                        this.paramsFunc.clear();
                    }
                    this.isFuncao = false;
                    this.isPrograma = false;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "while":
                if (lookAhead("while")){
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "do":
                if (lookAhead("do")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "if":
                if (lookAhead("if")){
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
            break;
            case "then":
                if (lookAhead("then")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "write":
                if (lookAhead("write")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "else":
                if (lookAhead("else")){
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ".":
                if (lookAhead(".")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "call":
                if (lookAhead("call")){
                    this.isChamada = true;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "return":
                if (lookAhead("return")){
                    this.analiseReturn = true;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "continue":
                if (lookAhead("continue")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "break":
                if (lookAhead("break")){
                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "not":
                if (lookAhead("not")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ",":
                if (lookAhead(",")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endif":
                if (lookAhead("endif")){
                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endelse":
                if (lookAhead("endelse")){
                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endwhile":
                if (lookAhead("endwhile")){
                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Integer":
                if (lookAhead("Integer")){
                    incrementaPosToken();
                    if (this.isFuncao){
                        this.paramsFunc.add(a.getNome());
                    }
                    if (this.isDeclaracao){
                        this.tipoAtual = a.getNome();
                    }
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Boolean":
                if (lookAhead("Boolean")){
                    incrementaPosToken();
                    if (this.isFuncao){
                        this.paramsFunc.add(a.getNome());
                    }
                    if (this.isDeclaracao){
                        this.tipoAtual = a.getNome();
                    }
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "true":
                if (lookAhead("true")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "false":
                if (lookAhead("false")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<variable_parameter>":
                if (lookAhead("Integer") || lookAhead("Boolean")){
                    this.isDeclaracao = false;
                    pilha.push("<identifier>");
                    pilha.push("<predefined_identifier>");
                 } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
        }
    }

    private void incrementaPosToken() throws SintaxError {
        if (linhaAtual < this.matrizDeSimbolos.getLinhaAtual()){
            if (colunaAtual >= this.matrizDeSimbolos.getColunaMax()){
                linhaAtual++;
                colunaAtual = 0;
            } else{
                colunaAtual++;
            }
        } else if (linhaAtual == this.matrizDeSimbolos.getLinhaAtual()){
            if (colunaAtual <= this.matrizDeSimbolos.getColunaAtual()){
                colunaAtual++;
            } if (colunaAtual > this.matrizDeSimbolos.getColunaAtual()){
                throw new SintaxError(this.linhaAtual, "Erro de tamnaho matriz");
            }
        }
    }


    private void analiseSintatica() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError {
        String atual = pilha.pop();
        while (!atual.equals("$")){
            analisaRegra(atual);
            atual = pilha.pop();

        }
        if (lookAhead("$")){
            System.out.println("SUCESSO!");
            for (Token a: this.matrizDeSimbolos.funcs()) {
                //System.out.println(a.getParametros());
                //System.out.println(a.getNome());
            }
        } else {
            System.out.println("ERRO DE SINTAXE!!!");
        }
    }

    private boolean isALetter(char caractere){
        return caractere == 'a' || caractere == 'b' || caractere == 'c' || caractere == 'd' || caractere == 'e' || caractere == 'f' ||
                caractere == 'g' || caractere == 'h' || caractere == 'i' || caractere == 'j' || caractere == 'l' || caractere == 'm' ||
                caractere == 'n' || caractere == 'o' || caractere == 'p' || caractere == 'q' || caractere == 'r' || caractere == 's' ||
                caractere == 't' || caractere == 'u' || caractere == 'v' || caractere == 'w' || caractere == 'x' || caractere == 'y' ||
                caractere == 'z' || caractere == 'A' || caractere == 'B' || caractere == 'C' || caractere == 'D' || caractere == 'E' ||
                caractere == 'F' || caractere == 'G' || caractere == 'H' || caractere == 'I' || caractere == 'J' || caractere == 'K' ||
                caractere == 'L' || caractere == 'M' || caractere == 'N' || caractere == 'O' || caractere == 'P' || caractere == 'Q' ||
                caractere == 'R' || caractere == 'S' || caractere == 'T' || caractere == 'U' || caractere == 'V' || caractere == 'W' ||
                caractere == 'X' || caractere == 'Y' || caractere == 'Z';

    }

    private boolean isPalavraReservada(String valor){
        return valor.equals("div") || valor.equals("or") || valor.equals("and") || valor.equals("not") || valor.equals("if") ||
                valor.equals("then") || valor.equals("else") || valor.equals("while") || valor.equals("do") || valor.equals("begin") ||
                valor.equals("end") || valor.equals("write") || valor.equals("procedure") || valor.equals("program") || valor.equals("break") ||
                valor.equals("continue") || valor.equals("return") || valor.equals("Boolean") || valor.equals("Integer") || valor.equals("true") ||
                valor.equals("false") || valor.equals("call") || valor.equals("endif") || valor.equals("endelse")|| valor.equals("endwhile") ;
    }

    private boolean isSpecialSymbol(String valor){
        return valor.equals("div") || valor.equals("or") || valor.equals("and") || valor.equals("not") || valor.equals("if") ||
                valor.equals("then") || valor.equals("else") || valor.equals("while") || valor.equals("do") || valor.equals("begin") ||
                valor.equals("end") || valor.equals("write") || valor.equals("procedure") || valor.equals("program") || valor.equals("break") ||
                valor.equals("continue") || valor.equals("return") || valor.equals("Boolean") || valor.equals("Integer") || valor.equals("call") ||
                valor.equals("endif") || valor.equals("endelse")|| valor.equals("endwhile") || valor.equals("Void") ;
    }

    private boolean isADigit(char caractere){
        return caractere == '0' || caractere == '1' || caractere == '2' || caractere == '3' || caractere == '4' || caractere == '5' ||
                caractere == '6' || caractere == '7' || caractere == '8' || caractere == '9';
    }

    private boolean isABoolean(String caractere){
        return caractere == "true" || caractere == "false";
    }
    private boolean lookAhead(String terminal){
        if (matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual) != null) {
            return matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().equals(terminal);
        } return false;
    }

    private boolean validarEscopo(Token b) throws JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, MissingReturnError {
        boolean valido = true;
        Token validacao = matrizDeSimbolos.buscarVariavel(b.getNome());
        if (this.isPrograma) {
            return false;
        }
        if (!isDeclaracao) {
            valido = false;
            if (validacao != null) {
                if (this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())) {
                    return true;
                } else {
                    if (this.analiseReturn){
                        if (this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())){
                            return true;
                        }
                    } else {
                        throw new EscopoInacessivelError(b.getLinha(), b.getNome());
                    }
                }
            }else {
                if (this.funcaoVarsEscopo != null && this.paramsEscopoFuncao.contains(b.getNome()) && !this.analiseReturn){
                    return true;
                } else {
                    Token t = matrizDeSimbolos.buscarToken(b.getLinhaMatriz(), b.getColunaMatriz());
                    if (!this.analiseReturn) {
                        if (t != null) {
                            if (t.getEscopo() != null) {
                                if (this.escopoGeral.getEscoposPai().contains(t.getEscopo().getId())) {
                                    return true;
                                } else {
                                    throw new EscopoInacessivelError(b.getLinha(), b.getNome());
                                }
                            } else {
                                throw new NaoDeclaradoError(b.getLinha(), b.getNome());
                            }
                        } else {
                            throw new NaoDeclaradoError(b.getLinha(), b.getNome());
                        }
                    } else {
                        if (t != null) {
                            if (t.getEscopo() != null) {
                                if (this.escopoGeral.getEscoposPai().contains(t.getEscopo().getId())) {
                                    if (this.funcaoVarsEscopo.getRetornoFuncao().equals(t.getRetornoFuncao())) {
                                        return true;
                                    } else {
                                        throw new MissingReturnError(funcaoVarsEscopo.getNome(), funcaoVarsEscopo.getRetornoFuncao(), t.getRetornoFuncao(), b.getLinha());
                                    }
                                } else {
                                    throw new EscopoInacessivelError(t.getLinha(), b.getNome());
                                }
                            } else {
                                if (this.funcaoVarsEscopo != null){
                                    if (this.paramsEscopoFuncao.contains(t.getNome())){
                                        int idex = 0;
                                        for (String par: paramsEscopoFuncao) {
                                            if (par.equals(t.getNome())){
                                                break;
                                            }
                                            idex++;
                                        }
                                        if (funcaoVarsEscopo.getParametros().get(idex).equals(funcaoVarsEscopo.getRetornoFuncao())){
                                            return true;
                                        } else {
                                            throw new NaoDeclaradoError(b.getLinha(), b.getNome());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } else {
            this.variavelAtual = b;
            String ta = this.tipoAtual;
            b.setRetornoFuncao(ta);
            if (validacao != null) {
                if (!this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())){
                    validacao.getEscopo().addEscopo(this.escopoGeral.getId());
                    return true;
                } else {
                    throw new JaDeclaradoError(validacao.getLinha(), validacao.getNome());
                }
            } else {
                if (this.funcaoVarsEscopo != null) {
                    if (!this.paramsEscopoFuncao.contains(b.getNome())) {
                        return true;
                    } else {
                        throw new JaDeclaradoError(funcaoVarsEscopo.getLinha(), b.getNome());
                    }
                }
            }
        }
        if (this.isDeclaracao){
            if (this.matrizDeSimbolos.buscarVariavel(b.getNome()) == null){
                this.matrizDeSimbolos.addVariavel(b);
                this.variavelAtual = b;
                String ta = this.tipoAtual;
                b.setRetornoFuncao(ta);
                this.isDeclaracao = false;
            }
        }
            return valido;
        }

        public boolean analisaEscopoFuncao(Token func) throws EscopoInacessivelError, FuncaoNaoDeclaradaError {
        Token validacao = this.matrizDeSimbolos.buscarFuncao(func.getNome());
            if (validacao != null) {
                if (validacao.isDeclarada()) {
                    if (this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())){
                        this.funcaoAtualChamada = validacao;
                        return true;
                    } else {
                        throw new EscopoInacessivelError(validacao.getLinha(), validacao.getNome());
                    }
                } else {
                    throw new FuncaoNaoDeclaradaError(func.getLinha(), func.getNome());
                }
            }
        return true;
        }

        public boolean analisaParam(Token param) throws NaoDeclaradoError, EscopoInacessivelError {
        Token validacao = this.matrizDeSimbolos.buscarVariavel(param.getNome());
        if (validacao != null){
            if (this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())) {
                return true;
            } else {
                throw new EscopoInacessivelError(param.getLinha(), param.getNome());
            }
        } else {
            throw new NaoDeclaradoError(param.getLinha(), param.getNome());
        }
        }

        public void analiseRepeat(Token param) throws ParamRepeatError {
            if (this.funcaoVarsEscopo == null){
                funcaoVarsEscopo = this.funcaoAtual;
            }
            if (paramsNames.size() > 0) {
                if (!paramsNames.contains(param.getNome())) {
                    paramsNames.add(param.getNome());
                    paramsEscopoFuncao.add(param.getNome());
                } else {
                    throw new ParamRepeatError(this.funcaoAtual.getNome(), param.getNome());
                }
            } else {
                paramsNames.add(param.getNome());
                paramsEscopoFuncao.add(param.getNome());
            }
        }

        public void analiseDeExpressaoAritmetica(){

        }

}
