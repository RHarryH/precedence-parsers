# Precedence parser

This tool presents the implementation of parsers for precedence grammars. Supported algorithms:
- shunting-yard (special case of parser for operator-precedence grammar)

## Usage

Please see help message running following command:
`java -jar precedence-parsers-<version> -h`

Examples:

- `java -jar precedence-parsers-<version> -i 2+2` - returns list of parsed tokens `[2, 2, ADD]`
- `java -jar precedence-parsers-<version> -i 2+2 -o rpn` - returns expression using reversed polish notation `2 2 +`
- `java -jar precedence-parsers-<version> -i 2+2 -o evaluated` - returns evaluated value `4`
- `java -jar precedence-parsers-<version> -i 2+2 -o expression-tree` - returns expression tree for the input expression

### Expression tree

Printing of expression tree uses UTF-8 characters. On Windows the console by default
is not able to print these values. To fix this run `chcp 65001` and run application
with `-Dfile.encoding=UTF-8` option.

```
ADD
├── 2
└── 2
```

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