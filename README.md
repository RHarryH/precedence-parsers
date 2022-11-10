## Shunting-yard algorithm

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