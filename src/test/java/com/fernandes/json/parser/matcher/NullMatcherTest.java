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

/**
 *
 */
class NullMatcherTest {

    private NullMatcher nullMatcher;

    private NullConsumer nullConsumer;

    @BeforeEach
    void setUp() {
        nullMatcher = new NullMatcher(nullConsumer = new NullConsumer());
    }

    @Test
    void whenMatchNull_ShouldMatch() throws IOException {
        boolean match = nullMatcher.match(ResourceProvider.createNull());
        assertThat(match).isTrue();
        assertThat(nullConsumer.getToken().getTokenType()).isEqualTo(TokenType.NULL);
    }

    @Test
    void whenMatchNull_ShouldNotMatch() throws IOException {
        boolean match = nullMatcher.match(ResourceProvider.createIncompleteNull());
        assertThat(match).isFalse();
    }

}

class NullConsumer implements Consumer<Token> {

    private Token token;

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}