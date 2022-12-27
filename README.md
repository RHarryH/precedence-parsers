# Precedence parsers

This tool presents the implementation of parsers for precedence grammars. Supported algorithms:
- operator-precedence parser (not available via CLI)
- simple precedence parser (including support for weak-precedence grammars)
- shunting-yard (special case of parser for operator-precedence grammar)

## Usage

Please see help message running following command:
`java -jar precedence-parsers-<version> -h`

Precedence parser examples:
- `java -jar precedence-parsers-<version> -i 2+2 -g grammar.txt -s expression_prime` - returns ordered list of productions to apply to get the input string `[factor -> [NUMBER_1:2], term -> [factor], term_prime -> [term], expression -> [term_prime], factor -> [NUMBER_2:2], term -> [factor], term_prime -> [term], expression -> [expression, ADD_1:+, term_prime], expression_prime -> [expression]]`
- `java -jar precedence-parsers-<version> -i 2+2 -g grammar.txt -s expression_prime -o derivation` - returns derivation from start symbol to input string by consequent productions' application `[[expression_prime], [expression], [expression, ADD_1:+, term_prime], [expression, ADD_1:+, term], [expression, ADD_1:+, factor], [expression, ADD_1:+, NUMBER_2:2], [term_prime, ADD_1:+, NUMBER_2:2], [term, ADD_1:+, NUMBER_2:2], [factor, ADD_1:+, NUMBER_2:2], [NUMBER_1:2, ADD_1:+, NUMBER_2:2]]`
- `java -jar precedence-parsers-<version> -i 2+2 -g grammar.txt -s expression_prime -o parse-tree` - returns parse tree for input string

Shunting yard examples:

- `java -jar precedence-parsers-<version> -i 2+2` - returns list of parsed tokens `[2, 2, ADD]`
- `java -jar precedence-parsers-<version> -i 2+2 -o rpn` - returns expression using reversed polish notation `2 2 +`
- `java -jar precedence-parsers-<version> -i 2+2 -o evaluated` - returns evaluated value `4`
- `java -jar precedence-parsers-<version> -i 2+2 -o expression-tree` - returns expression tree for the input expression

### Trees

Printing of trees uses UTF-8 characters. On Windows the console by default
is not able to print these values. To fix this run `chcp 65001` and run application
with `-Dfile.encoding=UTF-8` option.

#### Expression tree

Expression tree is a kind of abstract syntax tree (AST) for expressions. Can be generated using shunting-yard algorithm

```
ADD
├── 2
└── 2
```

#### Parse tree

Parse tree (aka concrete syntax tree) is one of the possible outputs of a parser. It is a tree representation of derivation
for specific input. It keeps much more data than AST, which usually is not needed for further processing. However, AST is
language specific, and therefore it is not supported in this solution.

```
expression_prime
└── expression
    ├── term_prime
    │   └── term
    │       └── factor
    │           └── NUMBER_2:2
    ├── ADD_1:+
    └── expression
        └── term_prime
            └── term
                └── factor
                    └── NUMBER_1:2
```

## Parsers

Parsers by default use precedence functions what limits error detection capabilities. It is not possible to control
it via CLI, however there is an API allowing to control that. There are some cases when precedence functions won't be
used anyway like parsing input for weak-precedence grammar 

Weak-precedence grammar is allowed only for simple precedence parsers. There are no extra constraints about the grammar
structure. Instead, the longest matching production is selected during parsing.

### Grammar file

Sample grammar file looks like below:

```
grammar SimplePrecedenceGrammar;

// terminals
ADD: \+;
MUL: \*;
NUMBER: [0-9];

// non-terminals
expression_prime: expression;
expression: expression ADD term_prime | term_prime;
term_prime: term;
term: term MUL factor | factor;
factor: NUMBER;
```

All expressions end with semicolon. 

`grammar` provides name for the grammar and can be located in any place in the file,
but it is recommended to have it as the first line.

Empty lines are ignored. Lines starting with `//` are treated as comments and are also ignored.

Terminals names have to start with uppercase. Then any alphanumeric characters + `_` are allowed. It is recommended to
use only uppercase letters and underscore character. After colon, regular expression is provided. It is used by lexer to
identify lexemes in input string. Terminal has to be defined before its first usage. Otherwise, there will be
an error thrown when loading the grammar.

Non-terminals names have to start with lowercase. Then any alphanumeric characters + `_` are allowed. It is recommended to
use only lowercase letters and underscore character. Non-terminals are defining left-hand side of a production. Right-hand
side is defined on the right side of the colon. Terminals and non-terminals have to be separated with at least single
space.

`|` character means an alternative. Below two entries are an equivalent:

```
expression: expression ADD term_prime | term_prime;`
```
```
expression: expression ADD term_prime;
expression: term_prime;
```

### Grammar requirements

Precedence parsers does not support all kind of context-free grammars. Both operator-precedence and simple precedence parsers
have similar but yet slightly different conditions.

Operator-precedence parser can parse operator-precedence grammars, which are in fact an operator grammars, which does not
have any conflicts in precedence table.

Operator grammar is a grammar, which met following conditions:
- does not have empty productions (guaranteed by grammar file validation)
- there are no two consecutive non-terminals in any production (ok: `expression ADD term_prime`, not ok: `expression term_prime ADD`)

Simple precedence parser is more general case of operator-precedence parser, which produces full parse tree. Grammar to be considered
as simple precedence grammar have to met following conditions:
- does not have empty productions (guaranteed by grammar file validation)
- all right-hand sides are different (unique)
- does not have conflicts in precedence table

However, when conflicts are detected, grammar might be considered as weak-precedence grammar. This kind of grammar accepts
conflict between ⋖ and ≐ precedences, merging them into single ⩿ precedence. Some sources add extra requirements for this
kind of grammars but this tool selects just the longest matching production. Therefore, both simple precedence and weak-precedence
grammars can be parsed by single parser implementation.

### Lexeme notation

When input is parsed, it is first split by lexer into lexemes. They can be treated as an "instance" of some terminal symbols.
In this tool following convention for lexemes was used:
`<TERMINAL_NAME>_<NUMBER_OF_LEXEME_INSTANCE_FOR_TERMINAL>:<LEXEME_VALUE>`

Example:
 - terminals: `A: a; B: b;`
 - input: `aab`
 - lexemes: `[A_1:a, A_2:a, B_1:b]`

## Shunting-yard algorithm

Shunting-yard algorithm is a method for parsing arithmetical expressions. It is a special case
of operator-precedence parser for operator-precedence grammar.

### Dealing with unary operators

This implementation supports only unary plus/minus.

First attempt was supporting unary minus only. It was done by
replacing common patterns with sub expressions, which eliminates the
presence of unary minus. This was:
- replacing `(-` with `(0-`
- replacing `,-` with `,0-` (for function arguments)
- replacing `-(` with `-1*(`
- replacing starting `-` with `0`

Although it worked fine I was not satisfied with direct string manipulation.

In next iteration I introduced simple lexer available in `Tokenizer` class.
It tries to detect unary operator if it is first symbol or preceded by another operator or left
parenthesis. Then it gets its own token, which later can be processed as
multiplication by `-1` in case of unary minus or ignored when unary plus.

Both supported unary operators have the same precedence as power symbol `^` and
are right associative.