package org.mbakkokom.simpleregex.enfa.evaluator;

import org.mbakkokom.simpleregex.exceptions.InvalidTokenError;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SpecialSymbolEntity;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SymbolEntity;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.TokenType;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Tokenizer;

import java.util.ArrayList;

public class EvaluatorString {
    public ArrayList<Token> value;

    private EvaluatorString(ArrayList<Token> value) {
        this.value = value;
    }

    public static EvaluatorString fromString(String s) {
        return new EvaluatorString(Tokenizer.readSymbolsFromString(s));
    }

    public static EvaluatorString fromTokens(ArrayList<Token> value) {
        return new EvaluatorString(new ArrayList<>(value));
    }

    public Token getTokenAt(int index) {
        return value.get(index);
    }

    public SymbolEntity getSymbolEntityAt(int index) {
        Token tok = value.get(index);
        TokenType t = tok.getType();

        if (t == TokenType.TOKEN_SYMBOL) {
            return new SymbolEntity(tok.getRawValue());
        } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_STRING) {
            return SpecialSymbolEntity.EmptyString;
        } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_SET) {
            return SpecialSymbolEntity.EmptySet;
        } else {
            throw new InvalidTokenError("invalid token " + t.toString(), tok);
        }
    }
}
