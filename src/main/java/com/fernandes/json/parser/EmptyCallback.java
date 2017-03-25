package com.fernandes.json.parser;

import java.util.function.Consumer;

/**
 *
 */
public class EmptyCallback implements Consumer<Token> {

    public static final EmptyCallback EMPTY_CALLBACK = new EmptyCallback();

    private EmptyCallback() {

    }

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {

    }
}
