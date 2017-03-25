package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.function.Consumer;

/**
 * Checks, if a string is true or false.
 */
public class BooleanMatcher extends AbstractMatcher {

    private static final String TRUE = "true";

    private static final String FALSE = "false";

    public BooleanMatcher(Consumer<Token> callback) {
        super(callback);
    }

    @Override
    public boolean match(PushbackReader pushbackReader) throws IOException {

        int pos = 0;
        String selectedCase = "";
        int c = pushbackReader.read();
        if (c == TRUE.charAt(0)) {
            selectedCase = TRUE;
        } else if (c == FALSE.charAt(0)) {
            selectedCase = FALSE;
        } else {
            pushbackReader.unread(c);
            return false;
        }
        pos = 1;

        do {
            c = pushbackReader.read();
        } while (pos < selectedCase.length() && selectedCase.charAt(pos++) == c);

        pushbackReader.unread(c);
        boolean ok = pos == selectedCase.length();
        if (ok) {
            callback.accept(new Token(Boolean.valueOf(selectedCase), TokenType.BOOLEAN));
        }
        return ok;
    }

    @Override
    protected void processCallback(StringBuilder builder) {

    }
}
