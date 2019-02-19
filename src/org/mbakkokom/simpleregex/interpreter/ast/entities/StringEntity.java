package org.mbakkokom.simpleregex.interpreter.ast.entities;

import java.util.Arrays;

public class StringEntity implements Entity {
    protected SymbolEntity[] string;

    public StringEntity() {
        this.string = new SymbolEntity[]{};
    }

    private StringEntity(boolean init) {
        if (!init) {
            this.string = null;
        } else {
            throw new IllegalCallerException("unexpected private initialization");
        }
    }

    public StringEntity(SymbolEntity[] string) {
        this.string = string.clone();
    }

    public static StringEntity fromSymbol(SymbolEntity s1) {
        StringEntity r = new StringEntity(false);

        r.string = new SymbolEntity[] { s1 };

        return r;
    }

    public static StringEntity fromString(StringEntity s1) {
        StringEntity r = new StringEntity(false);

        r.string = s1.getString();;

        return r;
    }


    public static StringEntity fromConcatSymbols(SymbolEntity s1, SymbolEntity s2) {
        StringEntity r = new StringEntity(false);

        r.string = new SymbolEntity[] { s1, s2 };

        return r;
    }

    public static StringEntity fromConcatStrings(StringEntity s1, StringEntity s2) {
        StringEntity r = new StringEntity(false);
        SymbolEntity[] a1 = s1.getString(), a2 = s2.getString();
        int l1 = a1.length, l2 = a2.length;

        r.string = Arrays.copyOf(a1, l1 + l2);
        System.arraycopy(a2, 0, r.string, l1, l2);

        return r;
    }

    public static StringEntity fromSymbolConcatString(SymbolEntity s1, StringEntity s2) {
        StringEntity r = new StringEntity(false);
        SymbolEntity a1 = s1;
        SymbolEntity[] a2 = s2.getString();
        int l2 = a2.length;

        r.string = new SymbolEntity[l2 + 1];
        r.string[0] = a1;
        System.arraycopy(a2, 0, r.string, 1, l2);

        return r;
    }

    public static StringEntity fromStringConcatSymbol(StringEntity s1, SymbolEntity s2) {
        StringEntity r = new StringEntity(false);
        SymbolEntity[] a1 = s1.getString();
        SymbolEntity a2 = s2;
        int l1 = a1.length;

        r.string = Arrays.copyOf(a1, l1 + 1);
        r.string[l1] = a2;

        return r;
    }

    public SymbolEntity[] getString() {
        return this.string.clone();
    }

    public void setString(SymbolEntity[] string) {
        this.string = string.clone();
    }

    public int length() {
        return this.string.length;
    }


    public EntityType type() {
        return EntityType.ENTITY_STRING;
    }

    public int precedence() {
        return 0;
    }
}
