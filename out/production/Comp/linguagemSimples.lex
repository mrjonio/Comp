
%%

%{


private Token createToken(String name, String value) {
    return new Token( name, value, yyline, yycolumn);
}

%}

%public
%class LexicalAnalyzer
%type Token
%line
%column

brancos = [\n| |\t]
nmrTelefone = [0-9][0-9]9(9|8)[0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]
placas = [A-Z][A-Z][A-Z]-[0-9][0-9][0-9][0-9]
isbn = "ISBN "[0-9][0-9][0-9]"-"[0-9][0-9]-[0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]-[0-9]

%%

{nmrTelefone} { return createToken("Telefone", yytext()); }
{placas} { return createToken("Placa",yytext()); }
{isbn} { return createToken("ISBN", yytext()); }
{brancos} { /**/ }

. { throw new RuntimeException("Caractere inválido " + yytext() + " na linha " + yyline + ", coluna " +yycolumn); }