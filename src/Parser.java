// Output created by jacc on Tue Sep 24 17:02:21 GFT 2019



import java.io.*;


class Parser implements parserTokens {
    private int yyss = 100;
    private int yytok;
    private int yysp = 0;
    private int[] yyst;
    protected int yyerrno = (-1);
    private Object[] yysv;
    private Object yyrv;

    public boolean parse() {
        int yyn = 0;
        yysp = 0;
        yyst = new int[yyss];
        yysv = new Object[yyss];
        yytok = (lexer.getToken()
                 );
    loop:
        for (;;) {
            switch (yyn) {
                case 0:
                    yyst[yysp] = 0;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 9:
                    switch (yytok) {
                        case type:
                            yyn = 4;
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 1:
                    yyst[yysp] = 1;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 10:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = 18;
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 2:
                    yyst[yysp] = 2;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 11:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = yyr1();
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 3:
                    yyst[yysp] = 3;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 12:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = yyr2();
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 4:
                    yyst[yysp] = 4;
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 13:
                    switch (yytok) {
                        case id:
                            yyn = 5;
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 5:
                    yyst[yysp] = 5;
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 14:
                    switch (yytok) {
                        case '=':
                            yyn = 6;
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 6:
                    yyst[yysp] = 6;
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 15:
                    switch (yytok) {
                        case num:
                            yyn = 7;
                            continue;
                        case saida:
                            yyn = 8;
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 7:
                    yyst[yysp] = 7;
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 16:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = yyr3();
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 8:
                    yyst[yysp] = 8;
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 17:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = yyr4();
                            continue;
                    }
                    yyn = 21;
                    continue;

                case 18:
                    return true;
                case 19:
                    yyerror("stack overflow");
                case 20:
                    return false;
                case 21:
                    yyerror("syntax error");
                    return false;
            }
        }
    }

    protected void yyexpand() {
        int[] newyyst = new int[2*yyst.length];
        Object[] newyysv = new Object[2*yyst.length];
        for (int i=0; i<yyst.length; i++) {
            newyyst[i] = yyst[i];
            newyysv[i] = yysv[i];
        }
        yyst = newyyst;
        yysv = newyysv;
    }

    private int yyr1() { // VARIAVEL : INT
        yysp -= 1;
        return 1;
    }

    private int yyr2() { // VARIAVEL : BOOLEAN
        yysp -= 1;
        return 1;
    }

    private int yyr3() { // INT : type id '=' num
        yysp -= 4;
        return 2;
    }

    private int yyr4() { // BOOLEAN : type id '=' saida
        {System.out.println("Expressao booleana: " + yysv[yysp-3]);}
        yysv[yysp-=4] = yyrv;
        return 3;
    }

    protected String[] yyerrmsgs = {
    };


private Lexer lexer;

    public Parser(Reader reader)
    {
        lexer = new Lexer(reader);
    }

    public void yyerror(String error)
    {
        System.err.println("Error: " + error);
    }

    public static void main(String args[]) throws IOException
    {
        System.out.println("Interactive evaluation:");

        Parser parser = new Parser(
            new InputStreamReader(System.in));

        parser.lexer.nextToken();
        parser.parse();
    }

}
