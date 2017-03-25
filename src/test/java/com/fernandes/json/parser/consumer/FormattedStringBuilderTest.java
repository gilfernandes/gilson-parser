package com.fernandes.json.parser.consumer;

import com.fernandes.json.parser.JsonParser;
import com.fernandes.json.parser.provider.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Scanner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FormattedStringBuilderTest {

    private FormattedStringBuilder formattedStringBuilder;

    @BeforeEach
    void setUp() {
        formattedStringBuilder = new FormattedStringBuilder("    ");
    }

    @Test
    void whenParseSimple_ShouldFormatCorrectly() throws IOException {
        JsonParser parser = new JsonParser(ResourceProvider.createCustomerOwnerKey(), formattedStringBuilder);
        assertThat(parser.parse()).isTrue();
        String lineBreak = String.format("%n");
        assertThat(formattedStringBuilder.toString()).isEqualTo("{" + lineBreak +
                "    \"_id\" : \"58d26f6ce4b000fd45f8b144\"," + lineBreak +
                "    \"customer\" : {" + lineBreak +
                "        \"owner\" : {" + lineBreak +
                "            \"key\" : {" + lineBreak +
                "                \"systemOwner\" : \"CP\"," + lineBreak +
                "                \"system\" : \"TU GROUP\"," + lineBreak +
                "                \"id\" : \"3109880\"" + lineBreak +
                "            }" + lineBreak +
                "        }" + lineBreak +
                "    }"  + lineBreak +
                "}");
    }

    @Test
    void whenParseComplex_ShouldFormatCorrectly() throws IOException {
        JsonParser parser = new JsonParser(ResourceProvider.createComplexCustomer(), formattedStringBuilder);
        assertThat(parser.parse()).isTrue();
        Scanner scanner =
                new Scanner(Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream("json/check/formattedFile.json")).useDelimiter("\\Z");
        String jsonCheck = scanner.next();
        assertThat(jsonCheck).isEqualTo(formattedStringBuilder.toString());
    }
}