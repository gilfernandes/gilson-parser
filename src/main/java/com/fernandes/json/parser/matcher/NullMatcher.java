package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Matches a null value.
 */
public class NullMatcher extends AbstractMatcher {

    private static final String NULL = "null";

    private static final int NULL_LENGTH = NULL.length();

    public NullMatcher(Consumer<Token> callback) {
        super(callback);
    }

    @Override
    public boolean match(PushbackReader pushbackReader) throws IOException {

        int c;
        int pos = -1;

        do {
            c = pushbackReader.read();
            pos = pos + 1;
        } while(NULL_LENGTH > pos && c > 0 && c == NULL.charAt(pos));

        boolean ok = NULL_LENGTH == pos;
        pushbackReader.unread(c);
        if(ok) {
            callback.accept(new Token(null, TokenType.NULL));
        }
        return ok;
    }
}
