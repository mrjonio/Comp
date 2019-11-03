import java.util.ArrayList;
import java.util.Stack;


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

    public Parser(int linhaAtual, int colunaAtual, IMatrizDeSimbolos matrizDeSimbolos) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError {
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
        iniciar();
    }

    private void iniciar() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError {
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


    private void analisaRegra(String regraAtual) throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError {
        arvore.add(regraAtual);
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
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<identifier>":
                if (this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().length() > 1) {
                    pilha.push("<letter_or_digit>");
                }

                if (!isSpecialSymbol(this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor())){
                    Token b = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    if (b.getLexema().equals("identifier") && !(isSpecialSymbol(b.getNome()))) {
                        if (validarEscopo(b)) {
                            Escopo setar = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                            b.setEscopo(setar);
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
                    isVariavel = true;

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
                        Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                        if (b.getLexema().equals("identifier") && (!isSpecialSymbol(b.getNome()))) {
                            if (validarEscopo(b)) {
                                Escopo setar = new Escopo(this.escopoGeral.getId(), this.escopoGeral.getEscoposPai());
                                b.setEscopo(setar);
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
                    this.isDeclaracao = false;
                    this.isPrograma = false;
                    isVariavel = false;
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                    this.escopoGeral.decrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);

                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<variable_declaration_part>":
                if (lookAhead("Integer") || lookAhead("Boolean")) {
                        this.isDeclaracao = true;
                        pilha.push("<variable_declaration_part_optional>");
                        pilha.push("<identifier>");
                        pilha.push("<predefined_identifier>");
                        isVariavel = true;
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
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                    pilha.push("<predefined_identifier>");
                    pilha.push("procedure");
                    this.isPrograma = true;
                } //Else: <empty>
                break;
            case "<procedure_declaration>:":
                pilha.push("<block>");
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
                    this.isPrograma = true;
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                    pilha.push("<variable_parameter>");
                    pilha.push(",");
                } //Else: <Empty>
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
                pilha.push("<parameters>");
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
                isVariavel = true;
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
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<multiplying_operator1>":
                if (lookAhead("*") || lookAhead("div") || lookAhead("and")) {
                    pilha.push("<expression>");
                    pilha.push("<multiplying_operator>");
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<identifier_or_value>":
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
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ")":
                if (lookAhead(")")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "+":
                if (lookAhead("+")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "-":
                if (lookAhead("-")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "=":
                if (lookAhead("=")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "*":
                if (lookAhead("*")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "div":
                if (lookAhead("div")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "or":
                if (lookAhead("or")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "and":
                if (lookAhead("and")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">":
                if (lookAhead(">")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ">=":
                if (lookAhead(">=")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<=":
                if (lookAhead("<=")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<>":
                if (lookAhead("<>")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<":
                if (lookAhead("<")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "\"":
                if (lookAhead("\"")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ":":
                if (lookAhead(":")){
                    this.isPrograma = false;
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "while":
                if (lookAhead("while")){
                    this.escopoGeral.incrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "do":
                if (lookAhead("do")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "if":
                if (lookAhead("if")){
                    this.escopoGeral.incrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
            break;
            case "then":
                if (lookAhead("then")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "write":
                if (lookAhead("write")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "else":
                if (lookAhead("else")){
                    this.escopoGeral.incrementar();
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ".":
                if (lookAhead(".")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "call":
                if (lookAhead("call")){
                    this.isPrograma = true;
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "return":
                if (lookAhead("return")){
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "continue":
                if (lookAhead("continue")){
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "break":
                if (lookAhead("break")){
                    this.escopoGeral.decrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "not":
                if (lookAhead("not")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case ",":
                if (lookAhead(",")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endif":
                if (lookAhead("endif")){
                    this.escopoGeral.decrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endelse":
                if (lookAhead("endelse")){
                    this.escopoGeral.decrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "endwhile":
                if (lookAhead("endwhile")){
                    this.escopoGeral.decrementar();
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Integer":
                if (lookAhead("Integer")){
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);

                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "Boolean":
                if (lookAhead("Boolean")){
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "true":
                if (lookAhead("true")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "false":
                if (lookAhead("false")){
                    incrementaPosToken();
                } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
                    throw new SintaxError(a.getLinha(), a.getValor());
                }
                break;
            case "<variable_parameter>":
                if (lookAhead("Integer") || lookAhead("Boolean")){
                    pilha.push("<identifier>");
                    pilha.push("<predefined_identifier>");
                 } else {
                    Token a = this.matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual);
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


    private void analiseSintatica() throws SintaxError, JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError {
        String atual = pilha.pop();
        while (!atual.equals("$")){
            analisaRegra(atual);
            atual = pilha.pop();

        }
        if (lookAhead("$")){
            System.out.println("SUCESSO!");
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
                valor.equals("continue") || valor.equals("return") || valor.equals("Boolean") || valor.equals("Integer") || valor.equals("call") || valor.equals("endif") || valor.equals("endelse")|| valor.equals("endwhile") ;
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

    private boolean validarEscopo(Token b) throws JaDeclaradoError, NaoDeclaradoError, EscopoInacessivelError {
        boolean valido = true;
        Token validacao = matrizDeSimbolos.buscarToken(linhaAtual, colunaAtual);
        System.out.println(isPrograma);
        if (this.isPrograma) {
            return false;
        }
        if (!isDeclaracao) {
            valido = false;
            if (validacao != null) {
                System.out.println(validacao.getEscopo().getEscoposPai());
                System.out.println(escopoGeral.getEscoposPai());
                System.out.println(this.escopoGeral.getId());
                System.out.println(validacao.getNome());
                System.out.println(b.getNome());
                if (this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())) {
                    return true;
                } else {
                    throw new EscopoInacessivelError(b.getLinha(), b.getNome());
                }
            }else {
                throw new NaoDeclaradoError(b.getLinha(), b.getNome());
            }
        } else {
            if (validacao != null) {
                if (!this.escopoGeral.getEscoposPai().contains(validacao.getEscopo().getId())){
                    validacao.getEscopo().addEscopo(this.escopoGeral.getId());
                    return true;
                } else {
                    System.out.println(isDeclaracao);
                    throw new JaDeclaradoError(validacao.getLinha(), validacao.getNome());
                }
            }
        }
            return valido;
        }

}
