package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
class BooleanMatcherTest {

    private BooleanMatcher booleanMatcher;

    private BooleanConsumer booleanConsumer;

    @BeforeEach
    void setUp() {
        booleanConsumer = new BooleanConsumer();
        booleanMatcher = new BooleanMatcher(booleanConsumer);
    }

    @Test
    void whenMatchTrue_ShouldBeTrue() throws IOException {
        assertThat(booleanMatcher.match(ResourceProvider.createTrue())).isTrue();
        assertThat(booleanConsumer.getBool()).isTrue();
    }

    @Test
    void whenMatchFalse_ShouldBeFalse() throws IOException {
        assertThat(booleanMatcher.match(ResourceProvider.createFalse())).isTrue();
        assertThat(booleanConsumer.getBool()).isFalse();
    }

    @Test
    void whenMatchFail_ShouldBeFalse() throws IOException {
        assertThat(booleanMatcher.match(ResourceProvider.createFalseIncomplete())).isFalse();
    }

}

class BooleanConsumer implements Consumer<Token> {

    private Boolean bool;

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        bool = (Boolean) token.getObject();
    }

    public Boolean getBool() {
        return bool;
    }
}