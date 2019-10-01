import java.util.Stack;

public class Parser {
    private int linhaAtual;
    private int colunaAtual;
    private Stack<String> pilha;
    private IMatrizDeSimbolos matrizDeSimbolos;

    public Parser(int linhaAtual, int colunaAtual, IMatrizDeSimbolos matrizDeSimbolos) {
        this.linhaAtual = linhaAtual;
        this.colunaAtual = colunaAtual;
        this.pilha = new Stack<String>();
        this.matrizDeSimbolos = matrizDeSimbolos;
    }

    private void iniciar(){
        pilha.push("$");
        pilha.push("<program>");
        String regra;
        regra = pilha.pop();
        analisaRegra(regra);
        analiseSintatica();
    }


    private void analisaRegra(String regraAtual){
        switch (regraAtual) {
            case "<program>":
                pilha.push(".");
                pilha.push("<block>");
                pilha.push(";");
                pilha.push("<identifier>");
                pilha.push("program");
                break;
            case "program":
                if (lookAhead("program")) {
                    incrementaPosToken();
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                }
                break;
            case "<identifier>":
                pilha.push("<letter_or_digit>");
                pilha.push("<letter>");
                break;
            case "<letter>":
                if (!isPalavraReservada(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor())) {
                    if (isALetter(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(0))) {
                        if (matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().substring(1).length() > 0) {
                            String valorVerificado = matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().substring(1);
                            matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).setValor(valorVerificado);
                        } else {
                            incrementaPosToken();
                        }

                    } else {
                        System.out.println("EROOOOOOOOOOOOOOOOOOOOOOO");
                    }
                }
                break;
            case "<letter_or_digit>":
                if (!isPalavraReservada(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor())) {
                    boolean flag = true;
                    for (int i = 0; i < matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().length(); i++) {
                        if (!(isALetter(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(i)))
                                && !(isADigit(matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().charAt(i)))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        incrementaPosToken();
                    } else {
                        System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOO");
                    }
                }
                break;
            case ";":
                if (lookAhead(";")) {
                    incrementaPosToken();
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOO");
                }
                break;
            case "<block>":
                pilha.push("<statement_part>");
                pilha.push("<procedure_declaration_part>");
                pilha.push("<variable_declaration_part>");
                break;
            case "<statement_part":
                if (lookAhead("begin")) {
                    pilha.push(" <compound_statement>");
                } //Else: <Empty>
                break;
            case "<compound_statement>":
                pilha.push("end");
                pilha.push("<optional_compound>");
                pilha.push("<compound_block");
                pilha.push("<statement>");
                pilha.push("begin");
                break;
            case "begin":
                if (lookAhead("begin")) {
                    incrementaPosToken();
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOO");
                }
                break;
            case "<statement>":
                if (lookAhead("begin") || lookAhead("while") || lookAhead("if")) {
                    pilha.push("<structured_statement>");
                } else {
                    pilha.push("<simple_statement>");
                }
                break;
            case "<compound_block>":
                if (lookAhead(";")) {
                    pilha.push("<statement>");
                    incrementaPosToken();
                } //Else: <empty>, ou seja, nada rs
                break;
            case "<optional_compound>":
                if (lookAhead("return")) {
                    pilha.push("<return_statement>");
                    incrementaPosToken();
                } //Else: <empty>, só retira do topo da pilha mesmo
                break;
            case "end":
                if (lookAhead("end")) {
                    incrementaPosToken();
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOOOOOOO");
                }
                break;
            case "<variable_declaration_part>":
                if (lookAhead("Integer") || lookAhead("Boolean") || lookAhead("true") ||
                        lookAhead("false")) {
                    pilha.push(";");
                    pilha.push("<identifier>");
                    pilha.push("<predefined_identifier>");
                    // TEM QUE ADICIONAR O ESCOPO + VALOR SEMÂNTICO!!!!!!
                } //Else: <empty>
                break;
            case "<procedure_declaration_part>":
                if (lookAhead("procedure")) {
                    pilha.push(";");
                    pilha.push("<procedure_declaration>");
                    pilha.push(")");
                    pilha.push("<parameters>");
                    pilha.push("(");
                    pilha.push("<identifier>");
                    pilha.push("procedure");
                } //Else: <empty>
                break;
            case "<procedure_declaration>:":
                pilha.push("<block>");
                break;
            case "procedure":
                if (lookAhead("procedure")) {
                    incrementaPosToken();
                } else {
                    System.out.println("ERROOOOOOOOOOOOOOOOOOOO");
                }

                break;
            case "<simple_statement>":
                if (lookAhead("write")) {
                    pilha.push("<write_statement>");
                } else {
                    if (lookAhead("break") || lookAhead("continue")) {
                        pilha.push("<detour_statement>");
                    } else {
                        if (lookAhead("call")) {
                            pilha.push("<procedure_statement>");
                        } else {
                            pilha.push("<assignment_statement>");
                        }
                    }
                }
                break;
            case "<parameters>":
                pilha.push("<parameter>");
                pilha.push("<variable_declaration_part>");
                break;
            case "<parameter>":
                if (lookAhead(",")) {
                    pilha.push("<variable_declaration_part>");
                    pilha.push(",");
                } //Else: <Empty>
                break;
            case "<detour_statement>":
                pilha.push(";");
                if (lookAhead("break")) {
                    pilha.push("break");
                } else {
                    pilha.push("continue");
                }
                break;
            case "<assignment_statement>":
                pilha.push("<expression>");
                pilha.push("=");
                pilha.push("<variable>");
                break;
            case "<procedure_statement>":
                pilha.push("<procedure_identifier>");
                pilha.push("call");
                break;
            case "<procedure_identifier>":
                pilha.push("<identifier>");
                break;
            case "<write_statement>":
                pilha.push(";");
                pilha.push(")");
                pilha.push("<aspas>");
                pilha.push("<variable>");
                pilha.push("<aspas>");
                pilha.push("(");
                pilha.push("write");
                break;
            case "<structured_statement>":
                if (lookAhead("begin")) {
                    pilha.push("<compound_statement>");
                } else {
                    if (lookAhead("if")) {
                        pilha.push("<if_statement>");
                    } else {
                        pilha.push("<else_statement>");
                    }
                }
                break;
            case "<if_statement>":
                pilha.push("<else_statement>");
                pilha.push("<statement>");
                pilha.push("then");
                pilha.push("<expression>");
                pilha.push("if");
                break;
            case "<else_statement>":
                if (lookAhead("else")) {
                    pilha.push("<statement>");
                    pilha.push("else");
                } //Else: <empty>
                break;
            case "<while_statement>":
                pilha.push("<statement>");
                pilha.push("do");
                pilha.push("<expression>");
                pilha.push("while");
                break;

            //Antônio//
            case "<expression>":
                pilha.push("<simple_expression>");
                pilha.push("<complement_expression>");
                break;
            case "<complement_expression>":
                pilha.push("<relational_operator>");
                pilha.push("<simple_expression>");
                break;// Else: <empty>
            case "<simple_expression>":
                pilha.push("<term>");
                pilha.push("<adding_operator1>");
                break;
            case "<adding_operator1>":
                if (lookAhead("+") || lookAhead("-") || lookAhead("or")) {
                    pilha.push("<adding_operator>");
                    pilha.push("<term>");
                } else {
                    pilha.push("<simple_expression>")
                }
                break;// Else: <empty>
            case "<term>":
                pilha.push("<factor>");
                pilha.push("<multiplying_operator1>");
                break;
            case "<multiplying_operator1>":
                if (lookAhead("*") || lookAhead("div") || lookAhead("and")) {
                    pilha.push("<multiplying_operator>");
                    pilha.push("<factor>");
                } else {
                    pilha.push("<term>")
                }
                break;// Else: <empty>
            case "<factor>":
                if (lookAhead("(")) {
                    pilha.push("(");
                    incrementaPosToken();
                    pilha.push("<expression>");
                    pilha.push(")");
                    incrementaPosToken();
                } else if (lookAhead("not")) {
                    pilha.push("not");
                    incrementaPosToken();
                    pilha.push("<factor>");
                } else {
                    pilha.push("<variable>");
                }
                break;


            case "<relational_operator>":
                if (lookAhead("=")) {
                    pilha.push("=");
                    incrementaPosToken();
                }
                if (lookAhead("<>")) {
                    pilha.push("<>");
                    incrementaPosToken();
                }
                if (lookAhead("<")) {
                    pilha.push("<");
                    incrementaPosToken();
                }
                if (lookAhead(">")) {
                    pilha.push(">");
                    incrementaPosToken();
                }
                if (lookAhead("<=")) {
                    pilha.push("<=");
                    incrementaPosToken();
                }
                if (lookAhead(">=")) {
                    pilha.push(">=");
                    incrementaPosToken();
                }
                break;
            case "<adding_operator>":
                if (lookAhead("+")) {
                    pilha.push("+");
                    incrementaPosToken();
                }
                if (lookAhead("-")) {
                    pilha.push("-");
                    incrementaPosToken();
                }
                if (lookAhead("or")) {
                    pilha.push("or");
                    incrementaPosToken();
                }
                break;
            case "<multiplying_operator>":
                if (lookAhead("*")) {
                    pilha.push("*");
                    incrementaPosToken();
                }
                if (lookAhead("div")) {
                    pilha.push("div");
                    incrementaPosToken();
                }
                if (lookAhead("and")) {
                    pilha.push("and");
                    incrementaPosToken();
                }
                break;
            case "<variable>":
                pilha.push("<identifier>");
                break;
            case "<aspas>":
                pilha.push("\"");
                incrementaPosToken();
                break;
            case "<predefined_identifier>":
                if (lookAhead("Integer")) {
                    pilha.push("Integer");
                    incrementaPosToken();
                }
                if (lookAhead("Boolean")) {
                    pilha.push("Boolean");
                    incrementaPosToken();
                }
                if (lookAhead("true")) {
                    pilha.push("true");
                    incrementaPosToken();
                }
                if (lookAhead("false")) {
                    pilha.push("false");
                    incrementaPosToken();
                }
                break;
        }
    }

    private void incrementaPosToken(){
        if (linhaAtual >= matrizDeSimbolos.getLinhaMax()) {
            if (colunaAtual < matrizDeSimbolos.getColunaAtual()){
                colunaAtual++;
                linhaAtual = 0;
            } else {
                System.out.println("LANÇAR UM ERROOOOOOOOO");
            }
        }else {
            linhaAtual++;
        }
    }

    private void analiseSintatica(){
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
                valor.equals("false") || valor.equals("call");
    }

    private boolean isSpecialSymbol(String valor){
        return valor.equals("div") || valor.equals("or") || valor.equals("and") || valor.equals("not") || valor.equals("if") ||
                valor.equals("then") || valor.equals("else") || valor.equals("+") || valor.equals("-") || valor.equals("*") || valor.equals("=") ||
                valor.equals("<") || valor.equals(">") || valor.equals("<=") || valor.equals(">=") || valor.equals("(") || valor.equals(")") ||
                valor.equals(",") || valor.equals(";") || valor.equals(":") || valor.equals("do") || valor.equals("begin") || valor.equals("end") ||
                valor.equals("write") || valor.equals("procedure") || valor.equals("program") || valor.equals("break") || valor.equals("continue") ||
                valor.equals("return") || valor.equals("call");
        ;
    }

    private boolean isADigit(char caractere){
        return caractere == '0' || caractere == '1' || caractere == '2' || caractere == '3' || caractere == '4' || caractere == '5' ||
                caractere == '6' || caractere == '7' || caractere == '8' || caractere == '9';
    }

    private boolean lookAhead(String terminal){
        return matrizDeSimbolos.getTokenNaPosicao(linhaAtual, colunaAtual).getValor().equals(terminal);
    }

}
