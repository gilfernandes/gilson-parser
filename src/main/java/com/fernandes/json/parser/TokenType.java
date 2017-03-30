package com.fernandes.json.parser;

/**
 * Contains the enumeration of token types.
 */
public enum TokenType {

    STRING,
    INTEGER,
    LONG,
    BIG_INTEGER,
    BIG_DECIMAL,
    DOUBLE,
    OBJECT_START,
    OBJECT_END,
    ARRAY_START,
    ARRAY_END,
    KEY,
    QUOTE,
    SEPARATOR,
    COLON,
    BOOLEAN,
    NULL;
}
