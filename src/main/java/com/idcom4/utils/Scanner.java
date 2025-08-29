package com.idcom4.utils;

import java.util.List;

public class Scanner<T> {

    public interface Source<E> {
        E GetAt(int index);
        int GetSize();
    }

    private final Source<T> source;
    private int cursor = 0;

    public Scanner(List<T> list) {
        this.source = new Source<>() {
            public T GetAt(int index) { return list.get(index); }
            public int GetSize() { return list.size(); }
        };
    }

    public Scanner(T[] array) {
        this.source = new Source<>() {
            public T GetAt(int index) { return array[index]; }
            public int GetSize() { return array.length; }
        };
    }

    public Scanner(String str) {
        @SuppressWarnings("unchecked")
        Source<T> src = (Source<T>) new Source<Character>() {
            public Character GetAt(int index) { return str.charAt(index); }
            public int GetSize() { return str.length(); }
        };
        this.source = src;
    }

    public boolean IsEmpty() {
        return cursor < source.GetSize() - 1;
    }

    public T Peek() {
        if (cursor < source.GetSize()) {
            return source.GetAt(cursor);
        }
        return null;
    }

    public T Peek(int offset) {
        if (cursor + offset < source.GetSize()) {
            return source.GetAt(cursor + offset);
        }
        return null;
    }

    public T Consume() {
        if (cursor < source.GetSize()) {
            return source.GetAt(cursor++);
        }
        return null;
    }

    public int GetCursor() {
        return cursor;
    }
}
