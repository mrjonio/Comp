import java.io.*;

%%
%class Lexer
%implements parserTokens

%function yylex
%int

%{

private int token;

    public int getToken()
    {
        return token;
    }
    public int nextToken()
    {
        try
        {
            token = yylex();
        }
        catch (java.io.IOException e)
        {
            System.out.println(
                "IO exception occured:\n" + e);
        }
        return token;
    }

%}


blanks = [ \t\n]+

id = [A-Za-z]([0-9]|[A-Za-z])*

num = [0-9]*

equal = '='

type = Boolean | Integer

saida = true | false

%%

{blanks}        { /* ignore */ }

{equal} {return(equal);}
{type} {return(type);}
{saida} {return(saida);}
{num} {return(num);}
{id} {return(id);}