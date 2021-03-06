
grammar Basic;

@header{
package basic.parser;
}

@parser::members {
    public static String sourceFileName = null;

    public BasicParser( String name, TokenStream tokens )
    {
        this(tokens);
        sourceFileName = name;
    }
}

program
    : newlines? (subroutine newlines)*
    ;

subroutine
    : 'SUB' name=IDENT ('(' (params+=IDENT (',' params+=IDENT)*)? ')')?
      sequence 'END' 'SUB'
    ;

sequence
    : newlines (statement newlines)*
    ;

newlines
    : NL+
    ;

statement
    : 'LET' IDENT '=' expression                             # statLet
    | 'INPUT' (TEXT ',')? IDENT                              # statInput
    | 'PRINT' expression                                     # statPrint
    | 'IF' mcond=expression 'THEN' mseq=sequence
      ('ELSEIF' scond+=expression 'THEN' sseq+=sequence)*
      ('ELSE' aseq=sequence)? 'END' 'IF'                     # statIf
    | 'WHILE' expression sequence 'END' 'WHILE'              # statWhile
    | 'FOR' IDENT '=' from=expression 'TO' to=expression
      ('STEP' step=REAL)? sequence 'END' 'FOR'               # statFor
    | 'CALL' IDENT (expression (',' expression)*)?           # statCall
    ;

expression
    : IDENT     # variable
    | REAL      # real
    | TEXT      # text
    | value=('TRUE' | 'FALSE') # logic
    | '(' expression ')'                               # priority
    | IDENT '(' (expression (',' expression)*)? ')'    # apply
    | oper=('NOT' | '-' | '+') expression              # unary
    | <assoc=right> left=expression oper='^' right=expression          # power
    | left=expression oper=('*' | '/') right=expression                # multiply
    | left=expression oper=('+' | '-' | '&') right=expression          # addition
    | left=expression oper=('>' | '>=' | '<' | '<=') right=expression  # comparison
    | left=expression oper=('=' | '<>') right=expression               # equality
    | left=expression oper='AND' right=expression                      # conjunction
    | left=expression oper='OR' right=expression                       # disjunction
    ;


KW_LET    : 'LET';
KW_INPUT  : 'INPUT';
KW_PRINT  : 'PRINT';
KW_IF     : 'IF';
KW_THEN   : 'THEN';
KW_ELSEIF : 'ELSEIF';
KW_ELSE   : 'ELSE';
KW_END    : 'END';
KW_WHILE  : 'WHILE';
KW_FOR    : 'FOR';
KW_TO     : 'TO';
KW_STEP   : 'STEP';
KW_CALL   : 'CALL';
KW_TRUE   : 'TRUE';
KW_FALSE  : 'FALSE';

COMMENT
    : '\'' ~'\n'* -> skip
    ;

TEXT
    : '"'~'"'*'"'
    ;

IDENT
    : [a-zA-Z][a-zA-Z0-9]*('$' | '#' | '?')?
    ;

REAL
    : ('+'|'-')?[0-9]+('.'[0-9]+)?
    ;

NL  : '\n'
    ;

WS  : [ \t\r]+ -> skip
    ;

