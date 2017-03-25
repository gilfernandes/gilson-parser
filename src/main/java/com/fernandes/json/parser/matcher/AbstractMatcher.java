package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Parent class of all matchers.
 */
public abstract class AbstractMatcher {

    final Consumer<? super Token> callback;

    public AbstractMatcher(Consumer<Token> callback) {
        this.callback = callback;
    }

    protected void processCallback(StringBuilder builder) {

    }

    public abstract boolean match(PushbackReader pushbackReader) throws IOException;

    protected String convertBuilderToString(StringBuilder builder) {
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
