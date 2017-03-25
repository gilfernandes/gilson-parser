package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NumberMatcherTest {

    private NumberMatcher numberMatcher;

    private NumberConsumer consumer;

    @BeforeEach
    void setUp() {
        numberMatcher = new NumberMatcher(consumer = new NumberConsumer());
    }

    @Test
    void whenMatchInteger_ShouldMatch12345() throws IOException {
        try(PushbackReader reader = ResourceProvider.createNumber1Reader()) {
            boolean match = numberMatcher.match(reader);
            assertTrue(match);
            assertEquals(12345, consumer.getNumber());
        }
    }

    @Test
    void whenMatchDouble_ShouldMatchDouble() throws IOException {
        try(PushbackReader reader = ResourceProvider.createDoubleReader()) {
            boolean match = numberMatcher.match(reader);
            assertTrue(match);
            assertEquals(12.34, consumer.getNumber());
        }
    }

    @Test
    void whenNotMatch_ShouldReturnFalse() throws IOException {
        try(PushbackReader reader = ResourceProvider.createStringReader()) {
            boolean match = numberMatcher.match(reader);
            assertFalse(match);
        }
    }

    @Test
    void whenMatchNegative_ShouldReturnTrue() throws IOException {
        try(PushbackReader reader = ResourceProvider.createNegativeReader()) {
            boolean match = numberMatcher.match(reader);
            assertThat(match).isTrue();
            Number number = consumer.getNumber();
            assertThat(number).isNotNull();
            assertThat(number.intValue()).isEqualTo(-1234);
        }
    }

    @Test
    void whenMatchPositive_ShouldReturnTrue() throws IOException {
        try(PushbackReader reader = ResourceProvider.createPositiveReader()) {
            boolean match = numberMatcher.match(reader);
            assertThat(match).isTrue();
            Number number = consumer.getNumber();
            assertThat(number).isNotNull();
            assertThat(number.intValue()).isEqualTo(1234);
        }
    }

    @Test
    void whenMatchLong_ShouldReturnTrue() throws IOException {
        try(PushbackReader reader = ResourceProvider.createPositiveLongReader()) {
            boolean match = numberMatcher.match(reader);
            assertThat(match).isTrue();
            Number number = consumer.getNumber();
            assertThat(number).isNotNull();
            assertThat(number.longValue()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Test
    void whenMatchNegativeLong_ShouldReturnTrue() throws IOException {
        try(PushbackReader reader = ResourceProvider.createNegativeLongReader()) {
            boolean match = numberMatcher.match(reader);
            assertThat(match).isTrue();
            Number number = consumer.getNumber();
            assertThat(number).isNotNull();
            assertThat(number.longValue()).isEqualTo(Long.MIN_VALUE);
        }
    }

}

class NumberConsumer implements Consumer<Token> {

    private Number number;

    public Number getNumber() {
        return number;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param o the input argument
     */
    @Override
    public void accept(Token o) {
        switch (o.getTokenType()) {
            case DOUBLE:
                this.number = (Double) o.getObject();
                break;
            case INTEGER:
                this.number = (Integer) o.getObject();
                break;
            case LONG:
                this.number = (Long) o.getObject();
                break;
        }
    }
}