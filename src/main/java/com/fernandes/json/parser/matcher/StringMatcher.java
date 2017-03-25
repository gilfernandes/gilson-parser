package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.EmptyCallback;
import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.fernandes.json.parser.TokenType.QUOTE;

/**
 * Matches strings like e.g: "test" with proper double quotes.
 * Also handles escaped quotes.
 */
public class StringMatcher extends AbstractMatcher {

    private static final Map<Character, Character> REPLACE_MAP = new HashMap<>();

    static {
        REPLACE_MAP.put('b', '\b');
        REPLACE_MAP.put('f', '\f');
        REPLACE_MAP.put('n', '\n');
        REPLACE_MAP.put('r', '\r');
        REPLACE_MAP.put('t', '\t');
        REPLACE_MAP.put('\\', '\\');
        REPLACE_MAP.put('"', '"');
    }

    private SingleMatcher quoteSingleMatcher;

    public StringMatcher(Consumer<Token> callback) {
        super(callback);
        quoteSingleMatcher = new SingleMatcher('"', QUOTE, EmptyCallback.EMPTY_CALLBACK);
    }

    public boolean match(PushbackReader pushbackReader) throws IOException {
        if (!quoteSingleMatcher.match(pushbackReader)) {
            return false;
        }
        characters(pushbackReader);
        return quoteSingleMatcher.match(pushbackReader);
    }

    private boolean characters(PushbackReader pushbackReader) throws IOException {
        int c = -1;
        int previousC;
        int counter = 0;
        StringBuilder builder = new StringBuilder();
        do {
            previousC = c;
            c = pushbackReader.read();
            c = performReplacements(c, previousC, builder, pushbackReader);
            if(c == -1) {
                return false;
            }
            builder.append((char) c);
            counter++;
        } while (c != '"' || previousC == '\\');
        pushbackReader.unread(c);
        processCallback(builder);
        return counter - 1 > 0;
    }

    private int performReplacements(int c, int previousC, StringBuilder builder, PushbackReader pushbackReader) throws IOException {
        if ('\\' == previousC) {
            Character replace = REPLACE_MAP.get((char) c);
            if(replace != null) {
                deleteLast(builder);
                return replace;
            }
            else if ('u' == c) {
                return processUnicodeChars(builder, pushbackReader);
            }

        }
        return c;
    }

    private int processUnicodeChars(StringBuilder builder, PushbackReader pushbackReader) throws IOException {
        StringBuilder unicodeBuilder = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            int unicodeC = pushbackReader.read();
            if(unicodeC == -1) {
                return -1;
            }
            unicodeBuilder.append((char) unicodeC);
        }
        deleteLast(builder);
        return Integer.valueOf(unicodeBuilder.toString(), 16);
    }

    private void deleteLast(StringBuilder builder) {
        builder.deleteCharAt(builder.length() - 1);
    }

    @Override
    protected void processCallback(StringBuilder builder) {
        final String str = convertBuilderToString(builder);
        callback.accept(new Token(str, TokenType.STRING));
    }

}
