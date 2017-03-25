package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.function.Consumer;

public class SingleMatcher extends AbstractMatcher {

    private final char character;

    private final TokenType tokenType;

    public SingleMatcher(char character, TokenType tokenType, Consumer<Token> callback) {
        super(callback);
        this.character = character;
        this.tokenType = tokenType;
    }

    public boolean match(PushbackReader pushbackReader) throws IOException {
        int c = pushbackReader.read();
        final boolean charMatch = c == character;
        if(!charMatch) {
            pushbackReader.unread(c);
        }
        else {
            final StringBuilder append = new StringBuilder().append((char) c);
            processCallback(append);
        }
        return charMatch;
    }

    @Override
    protected void processCallback(StringBuilder builder) {
        callback.accept(new Token(builder.toString(), tokenType));
    }
}
