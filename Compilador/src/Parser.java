import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.io.IOException;
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
    private ArrayList<String> expressao;
    private ArrayList<String> paramsNames;
    private boolean analiseRep;
    private Token funcaoVarsEscopo;
    private ArrayList<String> paramsEscopoFuncao;
    private boolean analiseReturn;
    private boolean isLogica;
    private ArrayList<String> logica;
    private boolean isWrite;
    private boolean nPassar;
    private int complPar;
    private AritmeticOps espe;
    private ContadoresTraducao conts;

    public Parser(int linhaAtual, int colunaAtual, IMatrizDeSimbolos matrizDeSimbolos) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError, AritmeticError, DivisaoPorZeroError {
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
        this.isWrite = false;
        this.nPassar = false;
        this.complPar = 0;
        this.espe = new AritmeticOps();
        try {
            this.conts = new ContadoresTraducao();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iniciar();
    }

    private void iniciar() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError, AritmeticError, DivisaoPorZeroError {
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


    private void analisaRegra(String regraAtual) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError, AritmeticError, DivisaoPorZeroError {
        arvore.add(regraAtual);
        Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
        System.out.println( regraAtual + " " + pilha.peek() + " " + this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor());
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
                if (this.isFuncao && !this.isChamada){
                    if (funcaoAtual != null){
                        this.funcaoAtual.nomeParams.add(a.getNome());
                    }
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
                            this.conts.funcatt = a.getNome();
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
                                        this.conts.traduzida.add("param " + a.getNome());
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
                    this.conts.varatt = a.getNome() + " = ";
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
                        if (!this.isFuncao && !this.isChamada && isExpressao && !isLogica && !isWrite && !nPassar){
                            //System.out.println(a.getNome());
                            this.expressao.add(a.getNome());
                        }
                        if (isLogica){
                            this.logica.add(a.getNome());
                        }
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

                    if (isExpressao && !isLogica){
                        for (int i = 0; i < this.expressao.size(); i++){
                            //System.out.print(this.expressao.get(i) + " ");
                        }
                        //System.out.println();
                        if (this.expressao.size() >= 1){
                            if (this.expressao.size() == 1){
                                if (this.variavelAtual.getRetornoFuncao().equals("Boolean")){
                                    if (!this.expressao.get(0).equals("true") && !this.expressao.get(0).equals("false")){
                                        throw new TypeError(a.getLinha(), "Boolean");
                                    }
                                } else {
                                    if (this.variavelAtual.getRetornoFuncao().equals("Integer")){
                                        if ((this.expressao.get(0).equals("true") || this.expressao.get(0).equals("false")))
                                        {
                                            throw new TypeError(a.getLinha(), "Integer");
                                        }
                                    }
                                }

                            } else {
                                if (this.variavelAtual.getRetornoFuncao().equals("Integer") && (this.expressao.contains("true") || this.expressao.contains("false"))) {
                                    throw new AritmeticError(this.variavelAtual.getNome(), a.getLinha());
                                } else if (this.variavelAtual.getRetornoFuncao().equals("Boolean")) {
                                    boolean fl = false;
                                    for (String ex : expressao) {
                                        if (!ex.equals("true") && !ex.equals("false")) {
                                            fl = true;
                                            break;
                                        }
                                    }
                                    if (fl) {
                                        throw new AritmeticError(this.variavelAtual.getNome(), a.getLinha());
                                    }
                                }
                            }
                            if (this.variavelAtual.getRetornoFuncao().equals("Integer")) {
                                for (String ex : expressao) {
                                    if (ex.equals("and") || ex.equals("or")) {
                                        throw new AritmeticError( variavelAtual.getNome(),a.getLinha());
                                    }
                                }
                            }
                            boolean tem = true;
                            int inicalPar = 0;
                            while (tem) {
                                tem = false;
                                for (int i = inicalPar; i < expressao.size(); i++) {
                                    if ((this.expressao.get(i).equals("*")  || this.expressao.get(i).equals("/")) &&
                                            !this.expressao.get(i - 1).equals(")") && !this.expressao.get(i + 1).equals("(")
                                    && (i - 2 > 0 && !this.expressao.get(i - 2).equals("("))) {
                                        tem = true;
                                        this.expressao.add(i + 2, ")");
                                        this.expressao.add(i - 1, "(");
                                        inicalPar = i + 2;
                                        break;
                                    }
                                }
                            }
                            int control = 1;
                            this.espe.temp.addAll(expressao);
                            this.espe.temp.add(")");
                            this.expressao.clear();
                            this.expressao.addAll(this.espe.temp);
                            //System.out.println(expressao);
                            boolean paren = true;
                            while (paren) {
                                paren = false;
                                int idex = 0;
                                boolean ant = false;
                                int fecho = 0;

                                for (int i = 0; i < expressao.size(); i++){
                                    if (this.expressao.get(i).equals("-") &&
                                            !this.expressao.get(i + 1).equals("(")){
                                        this.expressao.set(i, "+");
                                        String exp = "( -" + this.expressao.get(i + 1) + " )";
                                        this.expressao.set(i + 1, exp);
                                    }
                                }

                                for (int i = 0; i < expressao.size(); i++) {
                                    if (expressao.get(i).equals("(")){
                                        if (i - 1 > 0){
                                            if (!expressao.get(i - 1).equals("(")){
                                                ant = true;
                                            }
                                        }
                                        paren = true;
                                        idex = i;
                                    }
                                }

                                for (int i = idex; i < expressao.size(); i++){
                                    if (!expressao.get(i).equals(")")){
                                        if (!expressao.get(i).equals("(")) {
                                            this.espe.expressaoCorreta.add(expressao.get(i));
                                        }
                                    } else {
                                        expressao.remove(i);
                                        fecho = i - 1;
                                        break;
                                    }
                                }

                                if (this.expressao.get(idex).equals("(")){
                                    this.expressao.remove(idex);
                                }

                                boolean aindaTem = true;
                                while (aindaTem) {
                                    aindaTem = false;
                                    for (int i = idex; i < fecho; i++) {
                                        if (expressao.get(i).equals("*") || expressao.get(i).equals("/")) {
                                            if (expressao.get(i).equals("/")){
                                                if (expressao.get(i + 1).equals("0")){
                                                    throw new DivisaoPorZeroError(a.getLinha());
                                                }
                                            }
                                            aindaTem = true;
                                            String register = "t" + control;
                                            control++;
                                            String exp = register + " = " + expressao.get(i - 1)
                                                    + " " + expressao.get(i) + " " + expressao.get(i + 1);
                                            //System.out.println(exp);
                                            this.espe.pais.add(exp);
                                            expressao.set(i - 1, register);
                                            fecho--;
                                            fecho--;
                                            expressao.remove(i);
                                            expressao.remove(i);
                                            break;
                                        }
                                    }
                                }
                                //System.out.println(this.expressao);
                                if (fecho != idex){
                                    while (fecho > idex){
                                        fecho--;
                                    }
                                    if (ant){
                                        this.espe.expressaoCorreta.add(expressao.get(idex - 1));
                                    }
                                }

                            }
                            //System.out.println(expressao);
                            int c = this.expressao.size();
                            while(c > 1){
                                for (int i = 0; i < c; i++){
                                    if (expressao.get(i).equals("-") || expressao.get(i).equals("+")){
                                        String register = "t" + control;
                                        String exp = register + " = " + expressao.get(i - 1) + " " +
                                                expressao.get(i) + " " + expressao.get(i + 1);
                                        this.espe.pais.add(exp);
                                        this.expressao.set(i - 1, register);
                                        this.expressao.remove(i);
                                        this.expressao.remove(i);
                                        //System.out.println(exp);
                                        control++;
                                        c--;
                                        c--;
                                        break;
                                    }
                                }
                                if (c == 3 && expressao.get(0).equals("(")){
                                    expressao.remove(0);
                                    expressao.remove(1);
                                    c--;
                                    c--;
                                }
                            }
                            String fnal = this.variavelAtual.getNome() + " = " + expressao.get(0);
                            this.espe.pais.add(fnal);
                            //System.out.println(espe.pais);

                        }
                    }

                    if (this.conts.funcatt != null){
                        Token fu = this.matrizDeSimbolos.buscarFuncao(this.conts.funcatt);
                        this.conts.funcatt = " call " + fu.getNome() + "," + fu.getParametros().size();
                        if (this.conts.varatt != null){
                            this.conts.funcatt = this.conts.varatt + this.conts.funcatt;
                        }
                        this.conts.traduzida.add(this.conts.funcatt);
                    }
                    this.conts.funcatt = null;
                    this.conts.varatt = null;
                    this.conts.traduzida.addAll(this.espe.pais);
                    this.logica.clear();
                    this.espe.limpaTemp();
                    this.espe.expressaoCorreta.clear();
                    this.espe.pais.clear();
                    this.nPassar = false;
                    this.isExpressao = false;
                    this.expressao.clear();
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
                    this.conts.traduzida.add("Endfunc");
                    if (this.conts.funcRet.size() > 0) {
                        this.conts.traduzida.add(this.conts.funcRet.get(0));
                        this.conts.funcRet.remove(0);
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
                if (lookAhead(":")){
                    pilha.push("<procedure_final>");
                    pilha.push("<procedure_declaration>");
                    pilha.push(":");
                } else {
                    throw new FuncaoNaoDeclaradaError(a.getLinha(), funcaoAtual.getNome());
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
                if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor()) &&
                        (isALetter(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)) ||
                                isADigit(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0)))){
                    pilha.push("<identifier_or_value>");
                     String old = this.conts.traduzida.get(this.conts.traduzida.size() - 1);
                     old = old + " " + a.getNome();
                     this.conts.traduzida.set(this.conts.traduzida.size() - 1, old);
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
                } else if (lookAhead("*") || lookAhead("/") || lookAhead("and")){
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
                if (lookAhead("*") || lookAhead("/") || lookAhead("and")) {
                    pilha.push("<expression>");
                    pilha.push("<multiplying_operator>");
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<factor>":
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
                if (!this.isLogica && this.isExpressao) {
                    this.expressao.add(a.getNome());
                }
                if (lookAhead("+")) {
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    pilha.push("+");
                }
                if (lookAhead("-")) {
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    pilha.push("-");
                }
                if (lookAhead("or")) {
                    pilha.push("or");
                } //Else: empty
                break;
            case "<multiplying_operator>":
                if (!this.isLogica && this.isExpressao) {
                    this.expressao.add(a.getNome());
                }
                if (lookAhead("*")) {
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    pilha.push("*");
                }
                if (lookAhead("/")) {
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    pilha.push("/");
                }
                if (lookAhead("and")) {
                    pilha.push("and");
                } //Else: empty
                break;
            case "<predefined_identifier>":
                this.isExpressao = true;
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
                if (this.isExpressao && !this.isLogica && !this.nPassar){
                    this.expressao.add(a.getNome());
                }
                if (this.isLogica){
                    this.logica.add(a.getNome());
                }

                if (lookAhead("true")){
                    pilha.push("true");
                } else if(lookAhead("false")){
                    pilha.push("false");
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOO");
                    System.exit(1);
                }
                break;

            case "(":
                if (lookAhead("(")){
                    if (!this.isLogica && this.isExpressao) {
                        this.expressao.add(a.getNome());
                        this.complPar++;
                    }
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
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
                    this.isWrite = false;
                    this.analiseRep = false;
                    this.paramsNames.clear();
                    this.funcaoAtualChamada = null;
                    this.analiseParams = false;
                    this.isChamada = false;
                    if (!this.isLogica && this.isExpressao && this.complPar > 0) {
                        this.expressao.add(a.getNome());
                        this.complPar--;
                    }
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
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
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    this.nPassar = false;
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
            case "/":
                if (lookAhead("/")){
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "or":
                if (lookAhead("or")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "and":
                if (lookAhead("and")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">":
                if (lookAhead(">")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">=":
                if (lookAhead(">=")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<=":
                if (lookAhead("<=")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<>":
                if (lookAhead("<>")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<":
                if (lookAhead("<")){
                    if (this.isLogica){
                        this.logica.add(a.getNome());
                    }
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
                        //System.out.println(this.funcaoAtual.nomeParams);
                        this.funcaoAtual.setDeclarada(true);
                        ArrayList<String> tempPara = (ArrayList<String>) this.paramsFunc.clone();
                        this.funcaoAtual.setRetornoFuncao(tempPara.get(0));
                        tempPara.remove(0);
                        this.funcaoAtual.setParametros(tempPara);
                        this.paramsFunc.clear();
                        String got = "goto + " + "_func" + this.conts.funcAtual;
                        this.conts.traduzida.add(got);
                        this.conts.funcRet.add(this.conts.labelFunc + this.conts.funcAtual + ":");
                        this.conts.funcAtual++;
                        String name = funcaoAtual.getNome() + " : ";
                        this.conts.traduzida.add(name);
                        this.conts.traduzida.add("beginfunc:");
                    }
                    this.isLogica = false;
                    this.isFuncao = false;
                    this.isPrograma = false;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "while":
                if (lookAhead("while")){
                    this.isLogica = true;
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "do":
                if (lookAhead("do")){
                    incrementaPosToken();
                    if (logica.contains("+") || logica.contains("-") || logica.contains("*") || logica.contains("/")){
                        throw new AritmeticError("Aritmetico", a.getLinha());
                    }
                    for (int i = 0; i < logica.size(); i++){
                        if ( logica.size() == 3){
                            if (logica.size() == 3 && (logica.get(0).equals("(") && logica.get(2).equals(")"))){
                                logica.remove(0);
                                logica.remove(1);
                            }
                        }
                        if (logica.get(i).equals("<") || logica.get(i).equals(">") || logica.get(i).equals(">=")
                                || logica.get(i).equals("<=") || logica.get(i).equals("=")){
                            if (!isADigit(logica.get(i - 1).charAt(0))) {
                                Token op1 = this.matrizDeSimbolos.buscarVariavel(logica.get(i - 1));
                                if (op1 == null){
                                    int k = 0;
                                    for (int j = 0; j < paramsEscopoFuncao.size(); j++){
                                        if (paramsEscopoFuncao.get(j).equals(logica.get(i - 1))){
                                            k = j;
                                            break;
                                        }
                                    }
                                    if (!this.funcaoAtual.getParametros().get(k).equals("Integer")){
                                        throw new TypeError(a.getLinha(), "Boolean");
                                    }
                                }
                                else if (!op1.getRetornoFuncao().equals("Integer")){
                                    throw new TypeError(a.getLinha(), "Boolean");
                                }
                            }
                            if (!isADigit(logica.get(i + 1).charAt(0))) {
                                Token op2 = this.matrizDeSimbolos.buscarVariavel(logica.get(i + 1));
                                if (op2 == null){
                                    int k = 0;
                                    for (int j = 0; j < paramsEscopoFuncao.size(); j++){
                                        if (paramsEscopoFuncao.get(j).equals(logica.get(i + 1))){
                                            k = j;
                                            break;
                                        }
                                    }
                                    if (!this.funcaoAtual.getParametros().get(k).equals("Integer")){
                                        throw new TypeError(a.getLinha(), "Boolean");
                                    }
                                }
                                 else if (!op2.getRetornoFuncao().equals("Integer")){
                                    throw new TypeError(a.getLinha(), "Boolean");
                                }
                            }
                        }
                    }
                    if (this.logica.size() >= 1){
                        if (this.logica.size() == 1){
                            String exp = "t1 = " + this.logica.get(0);
                            this.espe.expressaoCorreta.add(exp);
                        } else {
                            int c = logica.size();
                            while (c > 1){
                                for (int i = 0; i < c; i++) {
                                    if (logica.get(i).equals("not")) {
                                        String exp = "!=";
                                        this.logica.set(i, exp);

                                    }
                                    if (this.logica.get(i).equals("=")) {
                                        String exp = "==";
                                        this.logica.set(i, exp);
                                    }
                                }
                                Boolean tem = true;
                                while (tem) {
                                    tem = false;
                                    for (int i = 0; i < c; i++) {
                                        if (logica.get(i).equals(">") || logica.get(i).equals(">=") || logica.get(i).equals("<") || logica.get(i).equals("<=")) {
                                            String register = "t" + this.espe.prioridade;
                                            this.espe.prioridade++;
                                            String exp = register + " = " + logica.get(i - 1) + " " + logica.get(i) + " " + logica.get(i + 1);
                                            logica.set(i - 1, register);
                                            logica.remove(i);
                                            logica.remove(i);
                                            c--;
                                            c--;
                                            this.espe.expressaoCorreta.add(exp);
                                            tem = true;
                                            break;
                                        }
                                    }
                                }
                                //System.out.println(logica);
                                for (int i = 0; i < c; i++){
                                    if ( logica.get(i).equals("and") || logica.get(i).equals("or") || logica.get(i).equals("==")
                                    || logica.get(i).equals("!=") || logica.get(i).equals("not")){
                                        String register = "t" + this.espe.prioridade;
                                        this.espe.prioridade++;
                                        String exp = register + " = " + logica.get(i - 1) + " " + logica.get(i) + " " + logica.get(i + 1);
                                        logica.set(i - 1, register);
                                        logica.remove(i);
                                        logica.remove(i);
                                        c--;
                                        c--;
                                        this.espe.expressaoCorreta.add(exp);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    this.conts.traduzida.add(this.conts.labelWhile + "" + this.conts.qtdWhile + ": ");
                    this.conts.whileFinais.add(this.conts.labelWhile + "" + this.conts.qtdWhile);
                    this.conts.qtdWhile++;
                    this.conts.traduzida.addAll(this.espe.expressaoCorreta);
                    this.conts.traduzida.add("if (" + this.logica.get(0) + ") goto "
                            + this.conts.stat + "" + this.conts.statAtual);
                    this.conts.iffinais.add(this.conts.stat + "" + this.conts.statAtual + ":");
                    this.conts.statAtual++;
                    this.conts.traduzida.add("ifnot (" + this.logica.get(0) + ") goto "
                            + this.conts.stat + "" + this.conts.statAtual);
                    this.conts.elsefinais.add(this.conts.stat + "" + this.conts.statAtual + ":");
                    this.conts.statAtual++;
                    String st = this.conts.iffinais.get(0);
                    this.conts.traduzida.add(st);
                    this.conts.iffinais.remove(0);
                    this.espe.prioridade = 0;
                    //System.out.println(espe.expressaoCorreta);
                    this.espe.expressaoCorreta.clear();
                    this.logica.clear();
                    isLogica = false;
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "if":
                if (lookAhead("if")){
                    this.conts.temElse++;
                    this.conts.ifStatement = true;
                    this.conts.expressaoCrua.add(a.getNome());
                    this.isLogica = true;
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
            break;
            case "then":
                if (lookAhead("then")){
                    if (logica.contains("+") || logica.contains("-") || logica.contains("*") || logica.contains("/")){
                        throw new AritmeticError("Aritmetico", a.getLinha());
                    }
                    for (int i = 0; i < logica.size(); i++){
                        if (logica.get(i).equals("<") || logica.get(i).equals(">") || logica.get(i).equals(">=")
                                || logica.get(i).equals("<=") || logica.get(i).equals("=")){
                            if (!isADigit(logica.get(i - 1).charAt(0))) {
                                Token op1 = this.matrizDeSimbolos.buscarVariavel(logica.get(i - 1));
                                if (op1 == null){
                                    int k = 0;
                                    for (int j = 0; j < paramsEscopoFuncao.size(); j++){
                                        if (paramsEscopoFuncao.get(j).equals(logica.get(i - 1))){
                                            k = j;
                                            break;
                                        }
                                    }
                                    if (!this.funcaoAtual.getParametros().get(k).equals("Integer")){
                                        throw new TypeError(a.getLinha(), "Boolean");
                                    }
                                }
                                else if (!op1.getRetornoFuncao().equals("Integer")){
                                    throw new TypeError(a.getLinha(), "Boolean");
                                }
                            }
                            if (!isADigit(logica.get(i + 1).charAt(0))) {
                                Token op2 = this.matrizDeSimbolos.buscarVariavel(logica.get(i + 1));
                                if (op2 == null){
                                    int k = 0;
                                    for (int j = 0; j < paramsEscopoFuncao.size(); j++){
                                        if (paramsEscopoFuncao.get(j).equals(logica.get(i + 1))){
                                            k = j;
                                            break;
                                        }
                                    }
                                    if (!this.funcaoAtual.getParametros().get(k).equals("Integer")){
                                        throw new TypeError(a.getLinha(), "Boolean");
                                    }
                                }
                                else if (!op2.getRetornoFuncao().equals("Integer")){
                                    throw new TypeError(a.getLinha(), "Boolean");
                                }
                            }
                        }
                    }
                    if (this.logica.size() >= 1){
                        if (this.logica.size() == 1){
                            String exp = "t1 = " + this.logica.get(0);
                            this.espe.expressaoCorreta.add(exp);
                        } else {
                            int c = logica.size();
                            while (c > 1){
                                for (int i = 0; i < c; i++) {
                                    if (logica.get(i).equals("not")) {
                                        String exp = "!=";
                                        this.logica.set(i, exp);
                                    }
                                    if (this.logica.get(i).equals("=")) {
                                        String exp = "==";
                                        this.logica.set(i, exp);
                                    }
                                }
                                Boolean tem = true;
                                while (tem) {
                                    tem = false;
                                    for (int i = 0; i < c; i++) {
                                        if (logica.get(i).equals(">") || logica.get(i).equals(">=") || logica.get(i).equals("<") || logica.get(i).equals("<=")) {
                                            String register = "t" + this.espe.prioridade;
                                            this.espe.prioridade++;
                                            String exp = register + " = " + logica.get(i - 1) + " " + logica.get(i) + " " + logica.get(i + 1);
                                            logica.set(i - 1, register);
                                            logica.remove(i);
                                            logica.remove(i);
                                            c--;
                                            c--;
                                            this.espe.expressaoCorreta.add(exp);
                                            tem = true;
                                            break;
                                        }
                                    }
                                }

                                for (int i = 0; i < c; i++){
                                    if (logica.get(i).equals("and") || logica.get(i).equals("or") || logica.get(i).equals("==")
                                    || logica.get(i).equals("!=")){
                                        String register = "t" + this.espe.prioridade;
                                        this.espe.prioridade++;
                                        String exp = register + " = " + logica.get(i - 1) + " " + logica.get(i) + " " + logica.get(i + 1);
                                        logica.set(i - 1, register);
                                        logica.remove(i);
                                        logica.remove(i);
                                        c--;
                                        c--;
                                        this.espe.expressaoCorreta.add(exp);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    this.conts.traduzida.add(this.conts.labelIf + "" + this.conts.qtdIf + ": ");
                    this.conts.qtdIf++;
                    this.conts.traduzida.addAll(this.espe.expressaoCorreta);
                    this.conts.traduzida.add("if (" + this.logica.get(0) + ") goto "
                            + this.conts.stat + "" + this.conts.statAtual);
                    this.conts.iffinais.add(this.conts.stat + "" + this.conts.statAtual + ":");
                    this.conts.statAtual++;
                    this.conts.gotoGeral.add("_generic" + this.conts.contGeral);
                    this.conts.contGeral++;
                    this.conts.gen++;
                    this.conts.traduzida.add("ifnot (" + this.logica.get(0) + ") goto "
                            + this.conts.stat + "" + this.conts.statAtual);
                    this.conts.elsefinais.add(this.conts.stat + "" + this.conts.statAtual + ":");
                    this.conts.statAtual++;
                    String st = this.conts.iffinais.get(0);
                    this.conts.traduzida.add(st);
                    this.conts.iffinais.remove(0);
                    this.espe.prioridade = 0;
                    this.espe.expressaoCorreta.clear();
                    //System.out.println(logica);
                    this.logica.clear();
                    isLogica = false;
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "write":
                if (lookAhead("write")){
                    this.isWrite = true;
                    this.conts.traduzida.add("LCALL _Print");
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "else":
                if (lookAhead("else")){
                    this.conts.traduzida.add("goto " + conts.gotoGeral.get(conts.gotoGeral.size() - 1));
                    this.conts.traduzida.add(this.conts.elsefinais.get(this.conts.elsefinais.size() - 1));
                    this.conts.elsefinais.remove(this.conts.elsefinais.size() - 1);
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ".":
                if (lookAhead(".")){
                    for (String ab : this.conts.traduzida){
                        try {
                            conts.out.append(ab);
                            conts.out.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        this.conts.out.close();
                        this.conts.ntemnomemais.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    this.conts.traduzida.add("goto next");
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "break":
                if (lookAhead("break")){
                    if (this.conts.gotoGeral.size() > 0){
                        this.conts.traduzida.add("goto " + this.conts.gotoGeral.get(this.conts.gotoGeral.size() - 1));
                    } else {
                        this.conts.traduzida.add("goto " + this.conts.elsefinais.get(this.conts.elsefinais.size() - 1));
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "not":
                if (lookAhead("not")){
                    if (this.isLogica){
                        logica.add(a.getNome());
                    }
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
                    this.conts.temElse--;
                    //System.out.println(conts.gotoGeral);
                    if (conts.elsefinais.size() > 0 && conts.temElse == 0){
                        this.conts.traduzida.add(this.conts.elsefinais.get(conts.elsefinais.size() - 1));
                        this.conts.elsefinais.remove(conts.elsefinais.size() - 1);
                        if (this.conts.gen > 0) {
                            this.conts.traduzida.add(conts.gotoGeral.get(conts.gotoGeral.size() - 1) + ": ");
                            this.conts.gotoGeral.remove(conts.gotoGeral.size() - 1);
                            this.conts.gen--;
                        }
                    }

                    this.escopoGeral.decrementar();

                    //System.out.println(this.conts.traduzida);
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endelse":
                if (lookAhead("endelse")){
                    this.escopoGeral.decrementar();
                    if (this.conts.gen > 0){
                        this.conts.traduzida.add(conts.gotoGeral.get(conts.gotoGeral.size() - 1) + ": ");
                        this.conts.gotoGeral.remove(conts.gotoGeral.size() - 1);
                        this.conts.gen--;
                    }
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endwhile":
                if (lookAhead("endwhile")){
                    this.conts.traduzida.add("goto " + this.conts.whileFinais.get(this.conts.whileFinais.size() - 1));
                    this.conts.whileFinais.remove(this.conts.whileFinais.size() - 1);
                    if (conts.elsefinais.size() > 0 && conts.temElse == 0){
                        this.conts.traduzida.add(this.conts.elsefinais.get(conts.elsefinais.size() - 1));
                        this.conts.elsefinais.remove(conts.elsefinais.size() - 1);
                    }

                    this.escopoGeral.decrementar();
                    incrementaPosToken();
                } else {
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Integer":
                if (lookAhead("Integer")){
                    incrementaPosToken();
                    this.nPassar = true;
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
                this.nPassar = true;
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


    private void analiseSintatica() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError, FuncaoNaoDeclaradaError, TypeError, OverflowParamsError, UnderflowParamsError, ParamRepeatError, MissingReturnError, RetornoIndesejadoError, AritmeticError, DivisaoPorZeroError {
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
            System.exit(1);
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
        return valor.equals("/") || valor.equals("or") || valor.equals("and") || valor.equals("not") || valor.equals("if") ||
                valor.equals("then") || valor.equals("else") || valor.equals("while") || valor.equals("do") || valor.equals("begin") ||
                valor.equals("end") || valor.equals("write") || valor.equals("procedure") || valor.equals("program") || valor.equals("break") ||
                valor.equals("continue") || valor.equals("return") || valor.equals("Boolean") || valor.equals("Integer") || valor.equals("true") ||
                valor.equals("false") || valor.equals("call") || valor.equals("endif") || valor.equals("endelse")|| valor.equals("endwhile") ;
    }

    private boolean isSpecialSymbol(String valor){
        return valor.equals("/") || valor.equals("or") || valor.equals("and") || valor.equals("not") || valor.equals("if") ||
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
                this.variavelAtual = validacao;
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
                                        this.conts.traduzida.add("return " + t.getNome() + ";");
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
            }  else {
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

        public void analiseRepeat(Token param) throws ParamRepeatError, JaDeclaradoError {
            if (this.funcaoVarsEscopo == null){
                funcaoVarsEscopo = this.funcaoAtual;
            }
            if (paramsNames.size() > 0) {
                if (!paramsNames.contains(param.getNome())) {
                    Token ns = this.matrizDeSimbolos.buscarVariavel(param.getNome());
                    if (ns != null){
                        throw new JaDeclaradoError(param.getLinha(), param.getNome());
                    }
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
