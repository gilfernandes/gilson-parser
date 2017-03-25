package com.fernandes.json.parser.matcher;

import com.fernandes.json.parser.Token;

import java.io.IOException;
import java.io.PushbackReader;
import java.math.BigInteger;
import java.util.function.Consumer;

import static com.fernandes.json.parser.TokenType.DOUBLE;
import static com.fernandes.json.parser.TokenType.INTEGER;
import static com.fernandes.json.parser.TokenType.LONG;

public class NumberMatcher extends AbstractMatcher {

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
                dotCounter++;
            }
        } while (Character.isDigit(c) || c == '.' || (counter == 1 && c == '-' || c == '+'));
        pushbackReader.unread(c);
        final boolean match = counter - 1 > 0 && dotCounter <= 1;
        if (match) {
            processCallback(builder);
        }
        return match;
    }

    @Override
    protected void processCallback(StringBuilder builder) {
        final String numberStr = convertBuilderToString(builder);
        if (numberStr.contains(".")) {
            callback.accept(new Token(new Double(numberStr), DOUBLE));
        } else {
            BigInteger bigInteger = new BigInteger(numberStr);
            if(bigInteger.bitCount() <= 32) {
                callback.accept(new Token(bigInteger.intValue(), INTEGER));
            }
            else if(bigInteger.bitCount() <= 64) {
                callback.accept(new Token(bigInteger.longValue(), LONG));
            }
        }
    }

}
