package org.mbakkokom.simpleregex.interpreter.ast;

import org.mbakkokom.simpleregex.interpreter.ast.entities.*;
import org.mbakkokom.simpleregex.exceptions.AbstractSyntaxTreeBuilderError;
import org.mbakkokom.simpleregex.exceptions.SyntaxTreeBuilderInternalError;
import org.mbakkokom.simpleregex.exceptions.SyntaxTreeBuilderSyntaxError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.TokenType;

import java.util.ArrayList;

/*
 * TODO. total rewrite for `appendToTree` functions.
 */
public class RegexSyntaxTreeBuilder {
    protected ArrayList<Token> tokens;

    /* there should be only ONE SetEntity on "this" RegexSyntaxTreeBuilder instance */
    protected SetEntity treeHead;
    protected Entity lastEntity;

    protected boolean danglingUnion = false;

    private RegexSyntaxTreeBuilder(ArrayList<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    public static RegexSyntaxTreeBuilder createBuilder(ArrayList<Token> tokens) {
        return new RegexSyntaxTreeBuilder(tokens);
    }

    /*
     * Currently, precedence is 'ignored' in the sense that it is assumed that the tree will have a specific structure.
     * For instance, all `ConcatEntity` will have the previous entity on the left side and the new 'right-operand'
     * entity on the right side; therefore any operation with higher precedence will need to operate the right-operand
     * only.
     */

    private Entity _getCurrentTreeHead() {
        int ln = this.treeHead.size();
        if (ln >= 1) {
            return this.treeHead.get(ln - 1);
        } else {
            return null;
        }
    }

    private void pushString(StringEntity s, Token token) {
        Entity head = _getCurrentTreeHead();
        if (head == null) {
            this.treeHead.add(s);
            this.lastEntity = s;
        } else if (danglingUnion) {
            this.treeHead.add(s);
            danglingUnion = false;
            this.lastEntity = s;
        } else {
            EntityType t = head.type();

            /*
            if (t == EntityType.ENTITY_CONCAT && ((ConcatEntity) head).getrValue().type() == EntityType.ENTITY_STRING) {

            } else */

            if (t == EntityType.ENTITY_STRING) {
                Entity n = StringEntity.fromConcatStrings((StringEntity) head, s);
                this.treeHead.replaceLastEntity(n);
                this.lastEntity = n;
            } else if (t == EntityType.ENTITY_SYMBOL || t == EntityType.ENTITY_SPECIAL_SYMBOL) {
                Entity n = StringEntity.fromSymbolConcatString((SymbolEntity) head, s);
                this.treeHead.replaceLastEntity(n);
                this.lastEntity = n;
            } else {
                ConcatEntity c = new ConcatEntity(head, s);
                this.treeHead.replaceLastEntity(c);
                this.lastEntity = s;
            }
        }
    }

    private void _pushClosure(Token tok) {
        Entity head = _getCurrentTreeHead();

        if (head == null) {
            throw new SyntaxTreeBuilderInternalError("invalid state when trying to push closure", tok);
        }

        Entity cur = head;
        ClosureEntity cl = null;
        int prTarget = (new ConcatEntity()).precedence();  // because Java interface does not support static method.

        if (cur.precedence() < prTarget) {
            cl = new ClosureEntity(cur);
            this.treeHead.replaceLastEntity(cl);
        } else {
            while (true) {
                Entity c = ((ConcatEntity) cur).getrValue();

                head = cur;
                cur = c;

                if (c.precedence() < prTarget) {
                    break;
                }
            }

            cl = new ClosureEntity(cur);
            ((ConcatEntity) head).setrValue(cl);
        }

        this.lastEntity = cl;
    }

    private void pushElse(Entity c, Token token) {
        Entity head = _getCurrentTreeHead();
        if (head == null) {
            this.treeHead.add(c);
            this.lastEntity = c;
        } else if (danglingUnion) {
            this.treeHead.add(c);
            danglingUnion = false;
            this.lastEntity = c;
        } else {
            ConcatEntity cn = new ConcatEntity(head, c);
            this.treeHead.replaceLastEntity(cn);
            this.lastEntity = c;
        }
    }

    private void pushEntity(Entity s, Token token) {
        switch(s.type()) {
            case ENTITY_STRING:
                pushString((StringEntity) s, token);
                break;
            case ENTITY_SET:
            case ENTITY_CONCAT:
                pushElse(s, token);
                break;
            default:
                throw new SyntaxTreeBuilderInternalError("unimplemented push_" + s.type().toString(), token);
        }

    }

    public RegexSyntaxTreeBuilder buildTree() throws AbstractSyntaxTreeBuilderError {
        int i = 0, length = this.tokens.size();
        this.treeHead = new SetEntity();

        ArrayList<SymbolEntity> lastString = new ArrayList<>();

        for (; i < length; i++) {
            Token tok = this.tokens.get(i);
            TokenType t = tok.getType();

            if (t == TokenType.TOKEN_INVALID) {
                throw new SyntaxTreeBuilderSyntaxError("unexpected TOKEN_INVALID", tok);
            } else if (t == TokenType.TOKEN_SYMBOL) {
                lastString.add(new SymbolEntity(tok.getRawValue()));
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_SET) {
                lastString.add(SpecialSymbolEntity.EmptySet);
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_STRING) {
                lastString.add(SpecialSymbolEntity.EmptyString);
            } else {
                if (t == TokenType.TOKEN_PAREN_OPEN) {
                    if (!lastString.isEmpty()) {
                        StringEntity s = new StringEntity(lastString.toArray(new SymbolEntity[]{}));
                        pushString(s, tok);
                        lastString.clear();
                    }

                    // find matching TOKEN_PAREN_CLOSE, use recursive call to build nested tree.
                    int open = i, close = i, level = 1;

                    for (i++; i < length; i++) {
                        TokenType t1 = this.tokens.get(i).getType();

                        if (t1 == TokenType.TOKEN_PAREN_OPEN) {
                            level++;
                        } else if (t1 == TokenType.TOKEN_PAREN_CLOSE) {
                            if (--level == 0) {
                                close = i;
                                break;
                            }
                        }
                    }

                    if (close - open == 0) {
                        throw new SyntaxTreeBuilderSyntaxError("unexpected end-of-file after TOKEN_PAREN_OPEN", tok);
                    } else if (level != 0) {
                        throw new SyntaxTreeBuilderSyntaxError("no matching TOKEN_PAREN_CLOSE", tok);
                    }

                    Entity nested = RegexSyntaxTreeBuilder.createBuilder(
                            new ArrayList<>(this.tokens.subList(open + 1, close))
                    ).buildTree().getTreeHead();

                    if (nested == null) {
                        throw new SyntaxTreeBuilderSyntaxError("unexpected result from parenthesis", tok);
                    } else {
                        pushEntity(nested, this.tokens.get(close));
                    }
                } else if (t == TokenType.TOKEN_PAREN_CLOSE) {
                    throw new SyntaxTreeBuilderSyntaxError("unexpected TOKEN_PAREN_CLOSE", tok);
                } else if (t == TokenType.TOKEN_OP_UNION) {
                    if (!lastString.isEmpty()) {
                        StringEntity s = new StringEntity(lastString.toArray(new SymbolEntity[]{}));
                        this.pushString(s, tok);
                        lastString.clear();
                    }

                    if (this.danglingUnion || this.lastEntity == null) {
                        throw new SyntaxTreeBuilderSyntaxError("unexpected TOKEN_OP_UNION", tok);
                    } else {
                        this.danglingUnion = true;
                    }
                } else if (t == TokenType.TOKEN_OP_CLOSURE) {
                    if (!lastString.isEmpty()) {
                        StringEntity s = new StringEntity(lastString.toArray(new SymbolEntity[]{}));
                        pushString(s, tok);
                        lastString.clear();
                    }

                    if (this.lastEntity == null) {
                        throw new SyntaxTreeBuilderSyntaxError("unexpected TOKEN_OP_CLOSURE", tok);
                    } else {
                        _pushClosure(tok);
                    }
                }
            }
        }

        if (length > 0) {
            Token lastToken = this.tokens.get(length - 1);

            if (!lastString.isEmpty()) {
                pushEntity(new StringEntity(lastString.toArray(new SymbolEntity[]{})), lastToken);
            }

            // TODO. perform checking on tree integrity,
            //       can use this.lastEntity and this.danglingUnion to check trivial integrity.

            if (this.danglingUnion) {
                throw new SyntaxTreeBuilderSyntaxError("unexpected end-of-file", lastToken);
            }
        }

        return this;
    }

    public Entity getTreeHead() {
        int size = this.treeHead.size();
        if (size <= 0) {
            return null;
        } else if (size == 1) {
            return this.treeHead.get(0);
        } else {
            return this.treeHead;
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
