package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.TokenType;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.assertj.core.api.Java6Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *
 */
class KeyMatcherTest {

    private KeyMatcher keyMatcher;

    private KeyConsumer keyConsumer;

    @BeforeEach
    void setUp() throws IOException {
        keyMatcher = new KeyMatcher(keyConsumer = new KeyConsumer());
    }

    @Test
    void match() throws IOException {
        assertThat(keyMatcher.match(ResourceProvider.createKeyReader())).isTrue();
        assertThat(keyConsumer.getStr()).isEqualTo("key");
        Java6Assertions.assertThat(keyConsumer.getTokenType()).isEqualTo(TokenType.KEY);
    }

}

class KeyConsumer implements Consumer<Token> {

    private String str;

    private TokenType tokenType;

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        str = token.getObject().toString();
        tokenType = token.getTokenType();
    }

    public String getStr() {
        return str;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}