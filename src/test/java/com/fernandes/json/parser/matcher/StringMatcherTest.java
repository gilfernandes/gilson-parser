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

/**
 *
 */
class StringMatcherTest {

    private StringMatcher stringMatcher;

    private StringConsumer stringConsumer;

    @BeforeEach
    void setUp() {
        stringMatcher = new StringMatcher(stringConsumer = new StringConsumer());
    }

    @Test
    void whenMatchString_ShouldMatch() throws IOException {
        try(PushbackReader reader = ResourceProvider.createStringReader()) {
            assertTrue(stringMatcher.match(reader));
            assertEquals("aassadd", stringConsumer.getString());
        }
    }

    @Test
    void whenMatchStringWithLineBreaks_ShouldMatchAndTranslateLineBreaks() throws IOException {
        try(PushbackReader reader = ResourceProvider.createStringReaderWithLineBreaks()) {
            assertTrue(stringMatcher.match(reader));
            assertThat(stringConsumer.getString()).isEqualTo("hello, \r\n" +
                    " world");
        }
    }

    @Test
    void whenMatchUnicodeWithLineBreaks_ShouldContainUnicodeCharacters() throws IOException {
        try(PushbackReader reader = ResourceProvider.createUnicodeString()) {
            assertTrue(stringMatcher.match(reader));
            final String extractedString = stringConsumer.getString();
            assertThat(extractedString.contains("\\u000a")).isFalse();
        }
    }

    @Test
    void whenMatchString_ShouldNotMatch() throws IOException {
        try(PushbackReader reader = ResourceProvider.createDoubleReader()) {
            assertThat(stringMatcher.match(reader)).isFalse();
        }
    }

}

class StringConsumer implements Consumer<Token> {

    private String string;

    /**
     * Performs this operation on the given argument.
     *
     * @param token the input argument
     */
    @Override
    public void accept(Token token) {
        this.string = (String) token.getObject();
    }

    public String getString() {
        return string;
    }
}