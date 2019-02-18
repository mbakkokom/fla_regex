package org.mbakkokom.simpleregex.interpreter.ast;

import org.mbakkokom.simpleregex.interpreter.ast.entities.*;
import org.mbakkokom.simpleregex.interpreter.exceptions.AbstractTreeBuilderError;
import org.mbakkokom.simpleregex.interpreter.exceptions.TreeBuilderSyntaxError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.TokenType;

import java.util.ArrayList;

public class AbstractSyntaxTreeBuilder {
    protected ArrayList<Token> tokens;

    protected Entity treeHead;
    protected Entity lastEntity;

    protected boolean danglingUnion = false;
    //protected Entity lastSetEntity;

    private AbstractSyntaxTreeBuilder(ArrayList<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    public static AbstractSyntaxTreeBuilder getInstance(ArrayList<Token> tokens) {
        return new AbstractSyntaxTreeBuilder(tokens);
    }

    protected void pushEntity(Entity e, Token token) {
        if (this.treeHead == null) {
            this.treeHead = e;
        } else {
            EntityType t = e.type();
            if (t == EntityType.ENTITY_STRING) {
                _pushStringEntity((StringEntity) e, token);
            }
        }

        /* TODO: more cases */

        throw new TreeBuilderSyntaxError(
                "unexpected case AbstractSyntaxTreeBuilder::pushEntity: " +
                        e.getClass().getTypeName() + ", toHead: " + this.treeHead.getClass().getTypeName(),
                token);
    }

    protected void _pushStringEntity(StringEntity s, Token token) {
        EntityType t = this.treeHead.type();
        if (t == EntityType.ENTITY_SYMBOL) {
            this.lastEntity = StringEntity.fromSymbolConcatString((SymbolEntity) this.treeHead, s);
            this.treeHead = this.lastEntity;
        } else if (t == EntityType.ENTITY_STRING) {
            this.lastEntity = StringEntity.fromConcatStrings((StringEntity) this.treeHead, s);
            this.treeHead = this.lastEntity;
        } else if (t == EntityType.ENTITY_SET) {
            ((SetEntity) this.treeHead).addEntity(s);
            this.lastEntity = this.treeHead;
        } else if (t == EntityType.ENTITY_CONCAT) {
            ConcatEntity c = (ConcatEntity) this.treeHead;
            if (c.getlValue() == null) {
                c.setlValue(s);
                this.lastEntity = this.treeHead;
            } else if (c.getrValue() == null) {
                c.setrValue(s);
                this.lastEntity = this.treeHead;
            } else {
                this.lastEntity =  new ConcatEntity(c, s);
                this.treeHead = this.lastEntity;
            }
        } else if (t == EntityType.ENTITY_CLOSURE) {
            this.lastEntity = new ConcatEntity(this.treeHead, s);
            this.treeHead = this.lastEntity;
        }

        /* TODO: more cases */

        throw new TreeBuilderSyntaxError(
                "unexpected case AbstractSyntaxTreeBuilder::pushEntity: " +
                        s.getClass().getTypeName() + ", toHead: " + this.treeHead.getClass().getTypeName(),
                token);
    }

    public AbstractSyntaxTreeBuilder buildTree() throws AbstractTreeBuilderError {
        int i = 0, length = this.tokens.size();
        this.treeHead = null;

        ArrayList<SymbolEntity> lastString = new ArrayList<>();

        for (; i < length; i++) {
            Token tok = this.tokens.get(i);
            TokenType t = tok.getType();

            if (t == TokenType.TOKEN_INVALID) {
                throw new TreeBuilderSyntaxError("unexpected TOKEN_INVALID", tok);
            } else if (t == TokenType.TOKEN_SYMBOL) {
                lastString.add(new SymbolEntity(tok.getRawValue()));
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_SET) {
                lastString.add(SpecialSymbolEntity.EmptySet);
            } else if (t == TokenType.TOKEN_SYMBOL_SPC_EMPTY_STRING) {
                lastString.add(SpecialSymbolEntity.EmptyString);
            } else {
                // TODO. put this on each case instead.
                if (!lastString.isEmpty()) {
                    pushEntity(new StringEntity((SymbolEntity[]) lastString.toArray()), tok);
                    lastString.clear();
                }

                if (t == TokenType.TOKEN_PAREN_OPEN) {
                    // TODO. find matching TOKEN_PAREN_CLOSE, use recursive call to build nested tree.
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
                        throw new TreeBuilderSyntaxError("unexpected end-of-file after TOKEN_PAREN_OPEN", tok);
                    } else if (level != 0) {
                        throw new TreeBuilderSyntaxError("no matching TOKEN_PAREN_CLOSE", tok);
                    }

                    Entity nested = AbstractSyntaxTreeBuilder.getInstance(
                            new ArrayList<Token>(this.tokens.subList(open + 1, close))
                    ).buildTree().getTreeHead();

                    // TODO. push to tree
                    if (nested == null) {
                        throw new TreeBuilderSyntaxError("unexpected result from parenthesis", tok);
                    } else {
                        pushEntity(nested, this.tokens.get(close - 1));
                    }
                } else if (t == TokenType.TOKEN_PAREN_CLOSE) {
                    throw new TreeBuilderSyntaxError("unexpected TOKEN_PAREN_CLOSE", tok);
                } else if (t == TokenType.TOKEN_OP_UNION) {
                    if (this.danglingUnion || this.lastEntity == null) {
                        throw new TreeBuilderSyntaxError("unexpected TOKEN_OP_UNION", tok);
                    } else {
                        /* 1. find lastEntity in tree
                         * 2. replace with SetEntity
                         * 3. add lastEntity to SetEntity
                         */
                    }
                } else if (t == TokenType.TOKEN_OP_CLOSURE) {
                    if (this.lastEntity == null) {
                        throw new TreeBuilderSyntaxError("unexpected TOKEN_OP_CLOSURE", tok);
                    } else {
                        // TODO.
                    }
                }
            }
        }

        Token lastToken = this.tokens.get(length - 1);

        if (!lastString.isEmpty()) {
            pushEntity(new StringEntity((SymbolEntity[]) lastString.toArray()), lastToken);
        }

        // TODO. perform checking on tree integrity,
        //       can use this.lastEntity and this.danglingUnion to check trivial integrity.

        if (this.danglingUnion) {
            throw new TreeBuilderSyntaxError("unexpected end-of-file", lastToken);
        }

        return this;
    }

    public Entity getTreeHead() {
        return this.treeHead;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
