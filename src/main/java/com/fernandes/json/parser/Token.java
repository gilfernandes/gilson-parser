package com.fernandes.json.parser;

import java.io.Serializable;

/**
 * Contains the type and a generic object.
 */
public class Token {

    private Serializable object;

    private TokenType tokenType;

    public Token(Serializable object, TokenType tokenType) {
        this.object = object;
        this.tokenType = tokenType;
    }

    public Serializable getObject() {
        return object;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
