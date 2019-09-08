import java_cup.runtime.*;

%%

%{


private TokenPascal createToken(String name, String value) {
    return new TokenPascal( name, value, yyline, yycolumn);
}

%}

%public
%class LexicalAnalyzer
%type PascalToken
%line
%column

program =   program <identifier> ; <block> .
block = <variable declaration part><procedure declaration part><statement part>
variable declaration part = <empty> |   var <identifier> ;
procedure declaration part  ={ <procedure declaration> ; }
procedure declaration = procedure <identifier> ; <block>
statement part =    <compound statement>
compound statement =    begin <statement>{  <statement> } [ return  <variable> | <constant> ] end
statement = <simple statement> | <structured statement>
simple statement =  <assignment statement> | <procedure statement> | <write statement> | <detour statement>
detour statement = break | continue
assignment statement =  <variable> := <expression>
procedure statement =   <procedure identifier>
procedure identifier =  <identifier>
write statement =   write ( <variable> | <constant> )
structured statement =  <compound statement> | <if statement> | <while statement>
if statement =  if <expression> then <statement> <else statement>
else statement =   else <statement> | <empty>
while statement =   while <expression> do <statement>
expression =    <simple expression> <complement expression>
complement expression = <relational operator> <simple expression> | <empty>
simple expression = <sign> <term> { <adding operator> <term> }
term =  factor { <multiplying operator> <factor> }
factor =    <variable> | <constant> | ( <expression> ) | not <factor>
relational operator =   = | <> | < | <= | >= | >
sign =  + | - | <empty>
adding operator =   + | - | or
multiplying operator =  * | div | and
variable =  <identifier>
constant =  <integer constant> | <character constant> | <constant identifier>
constant identifier =   <identifier>
identifier =    <letter> { <letter or digit> }
letter or digit =   <letter> | <digit>
integer constant =  <digit> { <digit> }
character constant =    '< any character other than ' >'  |  ''''
letter =
a | b | c | d | e | f | g | h | i | j | k | l | m | n | o |
p | q | r | s | t | u | v | w | x | y | z | A | B | C |
D | E | F | G | H | I | J | K | L | M | N | O | P
| Q | R | S | T | U | V | W | X | Y | Z
digit =
0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
special symbol =
+ | - | * | = | <> | < | > | <= | >= |
( | ) | [ | ] | := | . | , | ; | : | .. | div | or |
and | not | if | then | else | of | while | do |
begin | end | read | write | var | procedure | program | break | continue | return
<predefined identifier> ::=
integer | Boolean | true | false

%%

{inteiro} { return createToken("inteiro", yytext()); }
{program} { return createToken(yytext(), "");}
{brancos} { /**/ }

. { throw new RuntimeException("Caractere inválido " + yytext() + " na linha " + yyline + ", coluna " +yycolumn); }