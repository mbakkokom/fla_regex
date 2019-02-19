package org.mbakkokom.simpleregex.interpreter;

import org.mbakkokom.simpleregex.interpreter.ast.RegexSyntaxTreeBuilder;
import org.mbakkokom.simpleregex.interpreter.ast.entities.*;
import org.mbakkokom.simpleregex.interpreter.enfa.ENFAGraph;
import org.mbakkokom.simpleregex.interpreter.enfa.ENFAGraphBuilder;
import org.mbakkokom.simpleregex.interpreter.enfa.State;
import org.mbakkokom.simpleregex.interpreter.enfa.Transition;
import org.mbakkokom.simpleregex.interpreter.exceptions.SyntaxTreeBuilderSyntaxError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParserTest {
    public static void printTree(Entity e) {
        EntityType t = e.type();

        if (t == EntityType.ENTITY_SET) {
            ArrayList<Entity> s = (SetEntity) e;
            int i = 0, l = s.size() - 1;

            System.out.print('(');
            if (l > 0) {
                while (true) {
                    printTree(s.get(i));
                    if (i == l) {
                        break;
                    } else {
                        System.out.print('+');
                        i++;
                    }
                }
            }
            System.out.print(')');
        } else if (t == EntityType.ENTITY_CONCAT) {
            ConcatEntity c = (ConcatEntity) e;
            printTree(c.getlValue());
            printTree(c.getrValue());
        } else if (t == EntityType.ENTITY_CLOSURE) {
            ClosureEntity c = (ClosureEntity) e;
            printTree(c.getEntity());
            System.out.print('*');
        } else if (t == EntityType.ENTITY_STRING) {
            for (SymbolEntity s : ((StringEntity) e).getString()) {
                printTree(s);
            }
        } else if (t == EntityType.ENTITY_SYMBOL) {
            System.out.print(((SymbolEntity) e).getSymbolChar());
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            System.out.print("> ");
            while ((line = br.readLine()) != null) {
                try {
                    Entity head = RegexSyntaxTreeBuilder.createBuilder(Tokenizer.readFromString(line)).buildTree().getTreeHead();

                    System.out.print("= ");
                    if (head != null) {
                        printTree(head);
                    }
                    System.out.print('\n');

                    ENFAGraph graph = ENFAGraphBuilder.fromTreeHead(head).buildENFAGraph().getGraph();

                    for (State s : graph.states) {
                        System.out.println("[" + s.name + "]");

                        for (Transition t : s.getTransitions()) {
                            System.out.println("=> " + t.getNextState().name);
                        }

                        System.out.println();
                    }

                    System.out.println("COMPILE SUCCESS!");
                } catch (SyntaxTreeBuilderSyntaxError ex) {
                    System.out.println(ex.getClass() + ": " + ex.getMessage() + " at index " + ex.getToken().getIndex());
                    //throw ex;
                }
                System.out.print("> ");
            }
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }
}
