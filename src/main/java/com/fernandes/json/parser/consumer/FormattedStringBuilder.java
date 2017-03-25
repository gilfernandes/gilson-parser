package com.fernandes.json.parser.consumer;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.function.Consumer;

import static java.util.stream.IntStream.range;

/**
 * Used to format JSON on the go. This is another JSON formatter.
 */
public class FormattedStringBuilder implements Consumer<Token>  {

    private static final EnumMap<TokenType, String> TOKEN_MAP = new EnumMap<>(TokenType.class);

    static {
        TOKEN_MAP.put(TokenType.OBJECT_START, "{");
        TOKEN_MAP.put(TokenType.OBJECT_END, "}");
        TOKEN_MAP.put(TokenType.ARRAY_START, "[");
        TOKEN_MAP.put(TokenType.ARRAY_END, "]");
        TOKEN_MAP.put(TokenType.COLON, ":");
        TOKEN_MAP.put(TokenType.SEPARATOR, ",");
    }

    private String lineBreak = String.format("%n");

    private final StringBuilder builder = new StringBuilder();

    private final String indent;

    private TokenType previousTokenType;

    private int indentLength;

    public FormattedStringBuilder(String indent) {
        this.indent = indent;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        final Serializable object = token.getObject();
        final TokenType tokenType = token.getTokenType();
        switch (tokenType) {
            case OBJECT_START:
            case ARRAY_START:
                objectOrListStart(tokenType);
                break;
            case OBJECT_END:
            case ARRAY_END:
                objectOrListEnd(tokenType);
                break;
            case KEY:
                indent();
                appendString(object);
                break;
            case COLON:
                builder.append(" ").append(TOKEN_MAP.get(tokenType)).append(" ");
                break;
            case SEPARATOR:
                builder.append(TOKEN_MAP.get(tokenType)).append(lineBreak);
                break;
            case BOOLEAN:
            case DOUBLE:
            case LONG:
            case INTEGER:
            case NULL:
                builder.append(object);
                break;
            case STRING:
                appendString(object);
                break;
        }
        previousTokenType = tokenType;
    }

    private void appendString(Serializable object) {
        builder.append(String.format("\"%s\"", object));
    }

    private void objectOrListEnd(TokenType tokenType) {
        decrementIndent();
        builder.append(lineBreak);
        indent();
        builder.append(TOKEN_MAP.get(tokenType));

    }

    private void objectOrListStart(TokenType tokenType) {
        if(previousTokenType == TokenType.SEPARATOR) {
            indent();
        }
        builder.append(TOKEN_MAP.get(tokenType));
        switch (tokenType) {
            case OBJECT_START:
                builder.append(lineBreak);
                break;
            case ARRAY_START:
                builder.append(" ");
                break;
        }
        incrementIndent();
    }

    private void indent() {
        range(0, indentLength).forEach(i -> builder.append(indent));
    }

    private void decrementIndent() {
        indentLength--;
    }

    private void incrementIndent() {
        indentLength++;
    }

    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    public String getFormatted() {
        return builder.toString();
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
