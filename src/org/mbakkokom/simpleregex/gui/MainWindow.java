package org.mbakkokom.simpleregex.gui;

import org.mbakkokom.simpleregex.interpreter.ast.RegexSyntaxTreeBuilder;
import org.mbakkokom.simpleregex.interpreter.ast.entities.Entity;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SpecialSymbolEntity;
import org.mbakkokom.simpleregex.interpreter.enfa.ENFAGraph;
import org.mbakkokom.simpleregex.interpreter.enfa.ENFAGraphBuilder;
import org.mbakkokom.simpleregex.interpreter.enfa.State;
import org.mbakkokom.simpleregex.interpreter.enfa.Transition;
import org.mbakkokom.simpleregex.interpreter.exceptions.AbstractInterpreterError;
import org.mbakkokom.simpleregex.interpreter.exceptions.AbstractSyntaxTreeBuilderError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Tokenizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private JTextArea regexSpecTextArea;
    private JTabbedPane resultTabbedPane;
    private JPanel transitionTableTab;
    private JTable transitionTable;
    private JButton compileSpecButton;
    private JPanel mainPanel;
    private JPanel regexSpecPanel;
    private JTextArea compileLogTextArea;

    /* Interpreter instances */
    ArrayList<Token> regexTokens;
    Entity regexTreeHead;
    ENFAGraph regexGraph;

    public MainWindow() {
        compileSpecButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String spec = regexSpecTextArea.getText();
                clearCompileLog();

                regexTokens = null;
                regexTreeHead = null;
                regexGraph = null;

                if (spec.length() > 0) {
                    try {
                        printCompileLog("-> Tokenizing input...\n");
                        regexTokens = Tokenizer.readFromString(spec);

                        printCompileLog("-> Building expression tree...\n");
                        regexTreeHead = RegexSyntaxTreeBuilder.createBuilder(regexTokens).buildTree().getTreeHead();

                        printCompileLog("-> Building εNFA graph...\n");
                        regexGraph = ENFAGraphBuilder.fromTreeHead(regexTreeHead).buildENFAGraph().getGraph();
                    } catch (AbstractSyntaxTreeBuilderError ex) {
                        printCompileLog("\n!! ERROR !!\n");
                        printCompileLog(
                                ex.getClass().getTypeName() + ": " + ex.getMessage() +
                                        " at index " + ex.getIndex() + " (" + ex.getToken().getType().toString() + ")\n"
                        );

                        for (StackTraceElement st : ex.getStackTrace()) {
                            printCompileLog(st.toString() + "\n");
                        }
                    } catch (AbstractInterpreterError ex) {
                        printCompileLog("\n!! ERROR !!\n");
                        printCompileLog(
                                ex.getClass().getTypeName() + ": " + ex.getMessage() +
                                        " at index " + ex.getIndex() + "\n"
                        );

                        for (StackTraceElement st : ex.getStackTrace()) {
                            printCompileLog(st.toString() + "\n");
                        }
                    }

                    refreshData();
                } else {
                    printCompileLog(".. input is empty\n");
                }
            }
        });

        transitionTable.setShowGrid(true);

        add(mainPanel);

        setTitle("RegEx compiler, εNFA graph generator");
        setMinimumSize(new Dimension(500, 400));
    }

    private void refreshData() {
        transitionTable.setModel(new TransitionTableModel());
    }

    private void clearCompileLog() {
        this.compileLogTextArea.setText("");
    }

    private void printCompileLog(String str) {
        this.compileLogTextArea.append(str);
    }

    private class TransitionTableModel extends DefaultTableModel {
        @Override
        public int getColumnCount() {
            ENFAGraph g = regexGraph;
            if (g == null) {
                return 0;
            } else {
                return g.states.size() + 1;
            }
        }

        @Override
        public int getRowCount() {
            ENFAGraph g = regexGraph;
            if (g == null) {
                return 0;
            } else {
                return g.states.size();
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            ENFAGraph g = regexGraph;
            if (g == null) {
                return 0;
            } else if (col == 0) {
                return g.states.get(row).name;
            } else {
                State src = g.states.get(col - 1);
                State dest = g.states.get(row);

                ArrayList<Transition> list = src.getTransitionsTo(dest);
                int ln = list.size();

                if (ln <= 0) {
                    return null;
                } else if (ln == 1) {
                    Transition t = list.get(0);
                    char c = t.getSymbolChar();

                    if (c == SpecialSymbolEntity.EmptyString.getSymbolChar() || c == SpecialSymbolEntity.EmptySet.getSymbolChar()) {
                        if (t.isSpecialSymbol()) {
                            return c;
                        } else {
                            return "\\" + c;
                        }
                    } else {
                        return c;
                    }
                } else {
                    ArrayList<String> s = new ArrayList<>(ln);
                    for (Transition t : list) {
                        char c = t.getSymbolChar();

                        if (c == SpecialSymbolEntity.EmptyString.getSymbolChar() || c == SpecialSymbolEntity.EmptySet.getSymbolChar()) {
                            if (t.isSpecialSymbol()) {
                                s.add("" + c);
                            } else {
                                s.add("\\" + c);
                            }
                        } else {
                            s.add("" + c);
                        }
                    }

                    return "{ " + String.join(", ", s) + " }";
                }
            }
        }

        @Override
        public String getColumnName(int column) {
            ENFAGraph g = regexGraph;
            if (column == 0) {
                return null;
            } else {
                return g.states.get(column - 1).name;
            }
        }
    }
}
