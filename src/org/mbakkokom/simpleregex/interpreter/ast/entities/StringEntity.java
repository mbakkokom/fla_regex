package org.mbakkokom.simpleregex.interpreter.ast.entities;

import java.util.Arrays;

public class StringEntity extends Entity {
    protected char[] string;

    public StringEntity() {
        this.string = new char[]{};
    }

    private StringEntity(boolean init) {
        if (!init) {
            this.string = null;
        } else {
            throw new IllegalCallerException("unexpected private initialization");
        }
    }

    public StringEntity(char[] string) {
        this.string = string.clone();
    }

    public static StringEntity fromSymbol(SymbolEntity s1) {
        StringEntity r = new StringEntity(false);

        r.string = new char[] { s1.getSymbolChar() };

        return r;
    }

    public static StringEntity fromString(StringEntity s1) {
        StringEntity r = new StringEntity(false);

        r.string = s1.getString();;

        return r;
    }


    public static StringEntity fromConcatSymbols(SymbolEntity s1, SymbolEntity s2) {
        StringEntity r = new StringEntity(false);

        r.string = new char[] { s1.getSymbolChar(), s2.getSymbolChar() };

        return r;
    }

    public static StringEntity fromConcatStrings(StringEntity s1, StringEntity s2) {
        StringEntity r = new StringEntity(false);
        char[] a1 = s1.getString(), a2 = s2.getString();
        int l1 = a1.length, l2 = a2.length;

        r.string = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, r.string, l1, l2);

        return r;
    }

    public static StringEntity fromSymbolConcatString(SymbolEntity s1, StringEntity s2) {
        StringEntity r = new StringEntity(false);
        char a1 = s1.getSymbolChar();
        char[] a2 = s2.getString();
        int l2 = a2.length;

        r.string = new char[l2 + 1];
        r.string[0] = a1;
        System.arraycopy(a2, 0, r.string, 1, l2);

        return r;
    }

    public static StringEntity fromStringConcatSymbol(StringEntity s1, SymbolEntity s2) {
        StringEntity r = new StringEntity(false);
        char[] a1 = s1.getString();
        char a2 = s2.getSymbolChar();
        int l1 = a1.length;

        r.string = Arrays.copyOf(a1, l1 + 1);
        r.string[l1] = a2;

        return r;
    }

    public char[] getString() {
        return this.string.clone();
    }

    public void setString(char[] string) {
        this.string = string.clone();
    }

    public int length() {
        return this.string.length;
    }

    @Override
    public EntityType type() {
        return EntityType.ENTITY_SYMBOL;
    }
}
