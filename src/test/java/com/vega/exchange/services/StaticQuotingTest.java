package com.vega.exchange.services;

import com.vega.exchange.instruments.Quote;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaticQuotingTest {

    @Test
    void should_return_quote() {
        //give
        var instrumentId = randomUUID();
        var quote = new Quote(instrumentId, new Random().nextInt(1000), Instant.now());
        var quoting = new StaticQuoting(Map.of(instrumentId, quote));

        //when
        var result = quoting.getQuote(instrumentId);

        //then
        assertThat(result).isEqualTo(quote);
    }

    @Test
    void should_fail_for_unsupported_instrument() {
        //give
        var instrumentId = randomUUID();
        var quoting = new StaticQuoting(Map.of());

        //when
        assertThatThrownBy(() -> quoting.getQuote(instrumentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported instrument %s".formatted(instrumentId));
    }

}
