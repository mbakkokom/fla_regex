package org.mbakkokom.simpleregex.gui;

import org.mbakkokom.simpleregex.enfa.evaluator.Evaluator;
import org.mbakkokom.simpleregex.exceptions.ParseError;
import org.mbakkokom.simpleregex.interpreter.ast.RegexSyntaxTreeBuilder;
import org.mbakkokom.simpleregex.interpreter.ast.entities.Entity;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SpecialSymbolEntity;
import org.mbakkokom.simpleregex.enfa.graph.Graph;
import org.mbakkokom.simpleregex.enfa.graph.GraphBuilder;
import org.mbakkokom.simpleregex.enfa.graph.State;
import org.mbakkokom.simpleregex.enfa.graph.Transition;
import org.mbakkokom.simpleregex.exceptions.AbstractInterpreterError;
import org.mbakkokom.simpleregex.exceptions.AbstractSyntaxTreeBuilderError;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;
import org.mbakkokom.simpleregex.interpreter.tokenizer.Tokenizer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
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
    private JTextField evaluatorTextField;
    private JButton regexSpecEpsilonButton;
    private JButton evaluatorEpsilonButton;

    /* Interpreter instances */
    ArrayList<Token> regexTokens;
    Entity regexTreeHead;
    Graph regexGraph;
    Evaluator regexEvaluator;

    public MainWindow() {
        compileSpecButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String spec = regexSpecTextArea.getText();
                clearCompileLog();

                printCompileLog(".. Compiling at " + LocalDateTime.now().toString() + "\n");

                regexTokens = null;
                regexTreeHead = null;
                regexGraph = null;
                regexEvaluator = null;

                if (spec.length() > 0) {
                    try {
                        printCompileLog("-> Tokenizing input...\n");
                        regexTokens = Tokenizer.readFromString(spec);

                        printCompileLog("-> Building expression tree...\n");
                        regexTreeHead = RegexSyntaxTreeBuilder.createBuilder(regexTokens).buildTree().getTreeHead();

                        printCompileLog("-> Building εNFA graph...\n");
                        regexGraph = GraphBuilder.fromTreeHead(regexTreeHead).buildENFAGraph().getGraph();

                        printCompileLog("-> Creating evaluator...\n");
                        regexEvaluator = Evaluator.fromGraph(regexGraph);

                        printCompileLog(".. Finished compiling\n");
                    } catch (AbstractSyntaxTreeBuilderError ex) {
                        printCompileLog("\n!! ERROR !!\n");
                        printCompileLog(
                                ex.getClass().getTypeName() + ": " + ex.getMessage() +
                                        " at index " + ex.getIndex() + " (" + ex.getToken().getType().toString() + ")\n"
                        );

                        printCompileLog("\n!! Stack trace !!\n");

                        for (StackTraceElement st : ex.getStackTrace()) {
                            printCompileLog(st.toString() + "\n");
                        }
                    } catch (AbstractInterpreterError ex) {
                        printCompileLog("\n!! ERROR !!\n");
                        printCompileLog(
                                ex.getClass().getTypeName() + ": " + ex.getMessage() +
                                        " at index " + ex.getIndex() + "\n"
                        );

                        printCompileLog("\n!! Stack trace !!\n");

                        for (StackTraceElement st : ex.getStackTrace()) {
                            printCompileLog(st.toString() + "\n");
                        }
                    }

                    refreshData();
                } else {
                    printCompileLog(".. Input is empty\n");
                }
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionTable.setShowGrid(true);
        this.compileLogTextArea.setEditable(false);
        this.compileLogTextArea.setFont(Font.decode("Monospaced"));

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        setTitle("RegEx compiler, εNFA graph generator");
        setMinimumSize(new Dimension(500, 400));

        evaluatorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                evaluateSampleText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                evaluateSampleText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                evaluateSampleText();
            }
        });

        regexSpecEpsilonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regexSpecTextArea.append("ε");
            }
        });

        evaluatorEpsilonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                evaluatorTextField.setText(evaluatorTextField.getText() + "ε");
            }
        });
    }

    private void refreshData() {
        this.transitionTable.setModel(new TransitionTableModel());
        evaluateSampleText();
    }

    private void evaluateSampleText() {
        Evaluator e = regexEvaluator;
        if (e != null) {
            try {
                if (e.evaluate(evaluatorTextField.getText())) {
                    evaluatorTextField.setBackground(Color.green);
                } else {
                    evaluatorTextField.setBackground(Color.red);
                }
            } catch (ParseError ex) {
                evaluatorTextField.setBackground(Color.yellow);
            }
        }
    }

    private void clearCompileLog() {
        this.compileLogTextArea.setText("");
    }

    private void printCompileLog(String str) {
        this.compileLogTextArea.append(str);
    }

    private class TransitionTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public int getColumnCount() {
            Graph g = regexGraph;
            if (g == null) {
                return 0;
            } else {
                return g.states.size() + 1;
            }
        }

        @Override
        public int getRowCount() {
            Graph g = regexGraph;
            if (g == null) {
                return 0;
            } else {
                return g.states.size();
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            Graph g = regexGraph;
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
            Graph g = regexGraph;
            if (column == 0) {
                return null;
            } else {
                return g.states.get(column - 1).name;
            }
        }
    }
}
