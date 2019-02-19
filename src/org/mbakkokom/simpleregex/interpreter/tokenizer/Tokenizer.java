package org.mbakkokom.simpleregex.interpreter.tokenizer;

import org.mbakkokom.simpleregex.interpreter.exceptions.ParseError;

import java.util.ArrayList;

public class Tokenizer {
    public static ArrayList<Token> readFromString(String s) {
        ArrayList<Token> r = new ArrayList<>();
        char[] input = s.toCharArray();

        boolean lit = false;
        int i = 0, length = s.length();

        for (; i < length; i++) {
            char c = input[i];

            if (c == '\\') {
                lit = !lit;
            } else if (!lit) {
                switch (c) {
                    case 'ε':
                        r.add(new Token(TokenType.TOKEN_SYMBOL_SPC_EMPTY_STRING, c, i));
                        break;
                    case '∅':
                        r.add(new Token(TokenType.TOKEN_SYMBOL_SPC_EMPTY_SET, c, i));
                        break;
                    case '(':
                        r.add(new Token(TokenType.TOKEN_PAREN_OPEN, c, i));
                        break;
                    case ')':
                        r.add(new Token(TokenType.TOKEN_PAREN_CLOSE, c, i));
                        break;
                    case '+':
                        r.add(new Token(TokenType.TOKEN_OP_UNION, c, i));
                        break;
                    case '*':
                        r.add(new Token(TokenType.TOKEN_OP_CLOSURE, c, i));
                        break;
                    default:
                        r.add(new Token(TokenType.TOKEN_SYMBOL, c, i));
                        break;
                }
            } else {
                r.add(new Token(TokenType.TOKEN_SYMBOL, c, i));
            }
        }

        if (lit) {
            throw new ParseError("unexpected end-of-file after character '\\'", i);
        }

        return r;
    }
}
