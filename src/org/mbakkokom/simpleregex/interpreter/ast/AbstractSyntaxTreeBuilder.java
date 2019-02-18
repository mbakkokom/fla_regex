package org.mbakkokom.simpleregex.interpreter.ast;

import org.mbakkokom.simpleregex.interpreter.ast.entities.*;
import org.mbakkokom.simpleregex.interpreter.exceptions.AbstractTreeBuilderError;
import org.mbakkokom.simpleregex.interpreter.exceptions.TreeBuilderUnexpectedTokenError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.TokenType;

import java.util.ArrayList;

public class AbstractSyntaxTreeBuilder {
    protected ArrayList<Token> tokens;

    private AbstractSyntaxTreeBuilder(ArrayList<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    public static AbstractSyntaxTreeBuilder getInstance(ArrayList<Token> tokens) {
        return new AbstractSyntaxTreeBuilder(tokens);
    }

    protected Entity pushStringToHead(StringEntity s, Entity head, Token token) {
        if (head == null) {
            return s;
        } else {
            EntityType t = head.type();
            if (t == EntityType.ENTITY_SYMBOL) {
                return StringEntity.fromSymbolConcatString((SymbolEntity) head, s);
            } else if (t == EntityType.ENTITY_STRING) {
                return StringEntity.fromConcatStrings((StringEntity) head, s);
            } else if (t == EntityType.ENTITY_SET) {
                ((SetEntity) head).addEntity(s);
                return head;
            } else if (t == EntityType.ENTITY_CONCAT) {
                ConcatEntity c = (ConcatEntity) head;
                if (c.getlValue() == null) {
                    c.setlValue(s);
                    return head;
                } else if (c.getrValue() == null) {
                    c.setrValue(s);
                    return head;
                } else {
                    return new ConcatEntity(c, s);
                }
            } else if (t == EntityType.ENTITY_CLOSURE) {
                return new ConcatEntity(head, s);
            }
        }

        throw new TreeBuilderUnexpectedTokenError(
                "unexpected case AbstractSyntaxTreeBuilder::pushStringToHead(" +
                        s.getClass().getTypeName() + ", " + head.getClass().getTypeName() + ")",
                token);
    }

    public Entity buildTree() throws AbstractTreeBuilderError {
        int i = 0, length = this.tokens.size();
        Entity head = null;

        StringEntity lastString = new StringEntity();

        for (; i < length; i++) {
            Token tok = this.tokens.get(i);
            TokenType t = tok.getType();

            if (t == TokenType.TOKEN_INVALID) {
                throw new TreeBuilderUnexpectedTokenError("unexpected TOKEN_INVALID", tok);
            } else if (t == TokenType.TOKEN_SYMBOL) {
                lastString = StringEntity.fromStringConcatSymbol(lastString, new SymbolEntity(tok.getRawValue()));
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_SET) {
                lastString = StringEntity.fromStringConcatSymbol(lastString, SpecialSymbolEntity.EmptySet);
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_STRING) {
                lastString = StringEntity.fromStringConcatSymbol(lastString, SpecialSymbolEntity.EmptyString);
            } else {
                head = pushStringToHead(lastString, head, tok);
                lastString = new StringEntity();

                if (t == TokenType.TOKEN_PAREN_OPEN) {
                    // TODO. find matching TOKEN_PAREN_CLOSE, use recursive call to build nested tree.

                    // TODO. push to tree
                } else if (t == TokenType.TOKEN_PAREN_CLOSE) {
                    throw new TreeBuilderUnexpectedTokenError("unexpected TOKEN_PAREN_CLOSE", tok);
                } else if (t == TokenType.TOKEN_OP_UNION) {
                    // TODO.
                } else if (t == TokenType.TOKEN_OP_CLOSURE) {
                    // TODO.
                }
            }
        }

        return head;
    }
}
