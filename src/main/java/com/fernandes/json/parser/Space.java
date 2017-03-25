package com.fernandes.json.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Consumes all whitespace.
 */
public class Space {

    private final PushbackReader pushbackReader;

    public Space(PushbackReader pushbackReader) {
        this.pushbackReader = pushbackReader;
    }

    void space() throws IOException {
        process(pushbackReader);
    }

    public static void process(PushbackReader pushbackReader) throws IOException {
        int c;
        do {
            c = pushbackReader.read();
        } while (Character.isWhitespace(c));
        pushbackReader.unread(c);
    }
}
