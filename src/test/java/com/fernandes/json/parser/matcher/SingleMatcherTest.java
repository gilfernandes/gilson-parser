package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SingleMatcherTest {

    private SingleMatcher singleMatcher;

    private BracketConsumer callback;


    @BeforeEach
    void setUp() {
        callback = new BracketConsumer();
        singleMatcher = new SingleMatcher('{', TokenType.OBJECT_START, callback);
    }

    @Test
    void whenMatch_ShouldBeTrue() throws IOException {
        assertTrue(singleMatcher.match(ResourceProvider.createBracket()));
        assertTrue(callback.getTokenType() == TokenType.OBJECT_START);
        assertTrue(callback.getCharacter() == '{');
    }

    @Test
    void whenMatch_ShouldBeFalse() throws IOException {
        assertThat(singleMatcher.match(ResourceProvider.createNumber1Reader())).isFalse();
    }

}

class BracketConsumer implements Consumer<Token> {

    private char character;

    private TokenType tokenType;

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        character = token.getObject().toString().charAt(0);
        tokenType = token.getTokenType();
    }

    public char getCharacter() {
        return character;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}