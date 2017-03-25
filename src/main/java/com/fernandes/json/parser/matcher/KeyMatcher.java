package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.EmptyCallback;
import com.fernandes.json.parser.Space;
import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.function.Consumer;

/**
 * Matches used to match a full JSON key, including the colon.
 */
public class KeyMatcher extends AbstractMatcher {

    private final StringMatcher stringMatcher;

    private final SingleMatcher colonSingleMatcher;

    private final StringCallback stringCallback = new StringCallback();

    public KeyMatcher(Consumer<Token> callback) {
        super(callback);
        stringMatcher = new StringMatcher(stringCallback);
        colonSingleMatcher = new SingleMatcher(':', TokenType.COLON, callback);
    }

    @Override
    protected void processCallback(StringBuilder builder) {
        final String key = convertBuilderToString(builder);
        callback.accept(new Token(key, TokenType.KEY));
    }

    @Override
    public boolean match(PushbackReader pushbackReader) throws IOException {
        Space.process(pushbackReader);
        if (!stringMatcher.match(pushbackReader)) return false;
        super.callback.accept(new Token(stringCallback.getString(), TokenType.KEY));
        Space.process(pushbackReader);
        return colonSingleMatcher.match(pushbackReader);
    }

    private class StringCallback implements Consumer<Token> {

        private String string;

        /**
         * Performs this operation on the given argument.
         *
         * @param token the input argument
         */
        @Override
        public void accept(Token token) {
            string = (String) token.getObject();
        }

        public String getString() {
            return string;
        }
    }
}
