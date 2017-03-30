package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import static com.fernandes.json.parser.TokenType.*;

/**
 * Matches numbers in different formats: integers, doubles, big integers and scientific notation.
 */
public class NumberMatcher extends AbstractMatcher {

    public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    public static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);

    public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    public static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    public NumberMatcher(Consumer<Token> callback) {
        super(callback);
    }

    public boolean match(PushbackReader pushbackReader) throws IOException {
        int c;
        int counter = 0;
        int dotCounter = 0;
        StringBuilder builder = new StringBuilder();
        do {
            c = pushbackReader.read();
            counter++;
            builder.append((char) c);
            if (c == '.') {
                if(++dotCounter > 1) {
                    break;
                }
            }
            else if(c == 'e' || c == 'E') {
                c = processScientificNotation(pushbackReader, builder);
            }
        } while (Character.isDigit(c) || c == '.' || firstCharacterIsPlusMinus(c, counter));
        pushbackReader.unread(c);
        final boolean match = counter - 1 > 0 && dotCounter <= 1;
        if (match) {
            processCallback(builder);
        }
        return match;
    }

    private int processScientificNotation(PushbackReader pushbackReader, StringBuilder builder) throws IOException {
        int c;
        int counter = 0;
        do {
            counter++;
            c = pushbackReader.read();
            builder.append((char) c);
        } while (Character.isDigit(c) || firstCharacterIsPlusMinus(c, counter));
        return c;
    }

    private boolean firstCharacterIsPlusMinus(int c, int counter) {
        return counter == 1 && c == '-' || c == '+';
    }

    @Override
    protected void processCallback(StringBuilder builder) {
        final String numberStr = convertBuilderToString(builder);
        if (numberStr.contains(".")) {
            BigDecimal bigDecimal = new BigDecimal(numberStr);
            if(bigDecimal.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) <= 0
                    && bigDecimal.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) >= 0) {
                callback.accept(new Token(new Double(numberStr), DOUBLE));
            }
            else {
                callback.accept(new Token(bigDecimal, BIG_DECIMAL));
            }
        } else {
            BigInteger bigInteger = new BigInteger(numberStr);
            if(bigInteger.compareTo(MAX_INT) <= 0 && bigInteger.compareTo(MIN_INT) >= 0) {
                callback.accept(new Token(bigInteger.intValue(), INTEGER));
            }
            else if(bigInteger.compareTo(MAX_LONG) <= 0 && bigInteger.compareTo(MIN_LONG) >= 0) {
                callback.accept(new Token(bigInteger.longValue(), LONG));
            }
            else {
                callback.accept(new Token(bigInteger, BIG_INTEGER));
            }
        }
    }

}
