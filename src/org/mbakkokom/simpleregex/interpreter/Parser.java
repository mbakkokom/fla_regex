package org.mbakkokom.simpleregex.interpreter;

import org.mbakkokom.simpleregex.interpreter.ast.entities.*;
import org.mbakkokom.simpleregex.interpreter.exceptions.ParseError;

public class Parser {
    protected boolean ignoreSpaces;

    public Parser() {
        this(false);
    }

    public Parser(boolean ignoreSpaces) {
        this.ignoreSpaces = ignoreSpaces;
    }

    private Entity addNodeToTree(int index, Entity head, Entity node) {
        /*
         * (1) handles concat operations between symbols and/or strings automatically.
         * (2) todo.
         */
        if (head == null) {
            return node;
        } else if (node == null) {
            throw new ParseError(index, "cannot append a null node to the expression tree");
        } else {
            EntityType ht = head.type(), nt = node.type();
            if (ht == EntityType.ENTITY_SYMBOL) {
                SymbolEntity h = (SymbolEntity) head;
                if (nt == EntityType.ENTITY_SYMBOL) {
                    return StringEntity.fromConcatSymbols(h, (SymbolEntity) node);
                } else if (nt == EntityType.ENTITY_STRING) {
                    return StringEntity.fromSymbolConcatString(h, (StringEntity) node);
                } /* TODO: handles more types */
            } else if (ht == EntityType.ENTITY_STRING) {
                StringEntity h = (StringEntity) head;
                if (nt == EntityType.ENTITY_SYMBOL) {
                    return StringEntity.fromStringConcatSymbol(h, (SymbolEntity) node);
                } else if (nt == EntityType.ENTITY_STRING) {
                    return StringEntity.fromConcatStrings(h, (StringEntity) node);
                } /* TODO: handles more types */
            } /* TODO: handles more types */
        }

        throw new ParseError(index,"IMPOSSIBLE!");
    }

    private Entity _parseString(char[] input, int offset, int length) throws ParseError {
        Entity head = null;
        int i = offset, from = i, to = i;
        boolean lit = false;
        EntityType lastType = EntityType.ENTITY_INVALID;

        for (; i < length; i++) {
            char c = input[i];

            if (c == '\\') {
                lit = !lit;
            } else if (!lit) {
                if (c == '(') {
                    int begin = i++, end = begin, pcount = 1;

                    for (; i < length; i++) {
                        c = input[i];
                        if (c == '\\') {
                            lit = !lit;
                        } else if (!lit) {
                            if (c == '(') {
                                pcount++;
                            } else if (c == ')') {
                                if (--pcount == 0) {
                                    end = i;
                                    break;
                                } else if (pcount < 0) {
                                    throw new ParseError(i, "unexpected token");
                                }
                            }
                        }
                    }

                    int expr_length = begin - end;

                    if (expr_length == 0 || pcount != 0) {
                        throw new ParseError(i, "unexpected end-of-file");
                    } else if (expr_length == 1) {
                        throw new ParseError(i, "unexpected empty set");
                    }

                    head = this.addNodeToTree(i, head, this._parseString(input, begin + 1, expr_length - 1));
                }
            }
        }

        return head;
    }

    public Entity parseString(String input) {
        if (input.isEmpty()) {
            return null;
        } else {
            return this._parseString(input.toCharArray(), 0, input.length());
        }
    }
}
