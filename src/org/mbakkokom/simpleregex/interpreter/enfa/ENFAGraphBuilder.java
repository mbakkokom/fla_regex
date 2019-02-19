package org.mbakkokom.simpleregex.interpreter.enfa;

import org.mbakkokom.simpleregex.interpreter.ast.RegexSyntaxTreeBuilder;
import org.mbakkokom.simpleregex.interpreter.ast.entities.*;

public class ENFAGraphBuilder {
    private ENFAGraph graph;
    private Entity treeHead;

    private int _closureBeginCount = 0;
    private int _closureEndCount = 0;

    private int _setBeginCount = 0;
    private int _setEndCount = 0;

    private int _concatCount = 0;

    private int _stringCount = 0;

    public static ENFAGraphBuilder fromBuilder(RegexSyntaxTreeBuilder builder) {
        return ENFAGraphBuilder.fromTreeHead(builder.getTreeHead());
    }

    public static ENFAGraphBuilder fromTreeHead(Entity treeHead) {
        ENFAGraphBuilder f = new ENFAGraphBuilder();
        f.treeHead = treeHead;
        return f;
    }

    private void buildClosureGraph(State beginState, State endState, ClosureEntity entity) {
        int initialTransitionCount = 1;

        Entity child = entity.getEntity();
        boolean set = child.type() == EntityType.ENTITY_SET;

        if (set) {
            initialTransitionCount = ((SetEntity) child).size();
        }

        State closureBegin = this.graph.createState(2, "cB" + this._closureBeginCount++),
                closureMiddleBegin = this.graph.createState(initialTransitionCount, "cB" + this._closureBeginCount++),
                closureMiddleEnd = this.graph.createState(2, "cE" + this._closureEndCount++),
                closureEnd = this.graph.createState(1, "cE" + this._closureEndCount++);

        beginState.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, closureBegin));

        closureBegin.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, closureEnd));
        closureMiddleEnd.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, closureMiddleBegin));
        closureEnd.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, closureBegin));

        if (set) {
            _buildSetGraph(closureMiddleBegin, closureMiddleEnd, (SetEntity) child);
        } else {
            buildGraph(closureMiddleBegin, closureMiddleEnd, child);
        }
    }

    private void _buildSetGraph(State beginState, State endState, SetEntity setEntity) {
        for (Entity e : setEntity) {
            State setBegin = this.graph.createState(1, "sB" + this._setBeginCount++),
                    setEnd = this.graph.createState(1, "sE" + this._setEndCount++);

            beginState.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, setBegin));
            setEnd.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, endState));

            buildGraph(setBegin, setEnd, e);
        }
    }

    private void buildSetGraph(State beginState, State endState, SetEntity setEntity) {
        State setBegin = this.graph.createState(setEntity.size(), "sB" + this._setBeginCount++),
                setEnd = this.graph.createState(1, "sE" + this._setEndCount++);

        beginState.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, setBegin));
        setEnd.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, endState));

        _buildSetGraph(setBegin, setEnd, setEntity);
    }

    private void buildConcatGraph(State beginState, State endState, ConcatEntity concatEntity) {
        State nextState = this.graph.createState(1, "c" + this._concatCount++);

        buildGraph(beginState, nextState, concatEntity.getlValue());
        buildGraph(nextState, endState, concatEntity.getrValue());
    }

    private void buildStringGraph(State beginState, State endState, StringEntity stringEntity) {
        SymbolEntity[] symbols = stringEntity.getString();
        int l;

        if (symbols == null || (l = symbols.length) == 0) {
            buildGraph(beginState, endState, null);
        } else if (l == 1) {
            buildSymbolGraph(beginState, endState, symbols[0]);
        } else {
            int i = 0;

            State s = this.graph.createState(1, "s" + _stringCount++);

            for (; i < l; i++) {
                State n = this.graph.createState(1, "s" + _stringCount++);
                s.addTransition(this.graph.createTransition(symbols[i], n));

                s = n;
            }

            s.addTransition(this.graph.createTransition(symbols[i], endState));
        }
    }

    private void buildSymbolGraph(State beginState, State endState, SymbolEntity symbolEntity) {
        beginState.addTransition(this.graph.createTransition(symbolEntity, endState));
    }

    private void buildGraph(State beginState, State endState, Entity entity) {
        if (entity == null) {
            beginState.addTransition(this.graph.createTransition(SpecialSymbolEntity.EmptyString, endState));
        } else {
            switch (entity.type()) {
                case ENTITY_SET:
                    buildSetGraph(beginState, endState, (SetEntity) entity);
                    break;
                case ENTITY_CLOSURE:
                    buildClosureGraph(beginState, endState, (ClosureEntity) entity);
                    break;
                case ENTITY_CONCAT:
                    buildConcatGraph(beginState, endState, (ConcatEntity) entity);
                    break;
                case ENTITY_STRING:
                    buildStringGraph(beginState, endState, (StringEntity) entity);
                    break;
                case ENTITY_SYMBOL:
                case ENTITY_SPECIAL_SYMBOL:
                    buildSymbolGraph(beginState, endState, (SymbolEntity) entity);
                    break;
                default:
                    throw new NoSuchMethodError("cannot build graph from " + entity.type().toString());
            }
        }
    }

    public ENFAGraphBuilder buildENFAGraph() {
        this.graph = new ENFAGraph();

        this.graph.beginState = this.graph.createState(1, "begin");
        this.graph.endState = this.graph.createState(0, "end", true);

        this.buildGraph(this.graph.beginState, this.graph.endState, this.treeHead);

        return this;
    }

    public ENFAGraph getGraph() {
        return graph;
    }
}
